package com.romanidze.auction.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.romanidze.auction.entities.LotInformation._
import com.romanidze.auction.entities.LotRegistrar._

import scala.collection.immutable.Iterable
import scala.concurrent.{ExecutionContext, Future}

object LotInformationActor {
  def props(implicit timeout: Timeout, executor: ExecutionContext): Props = Props(
    new LotInformationActor
  )
}

class LotInformationActor(implicit timeout: Timeout, executor: ExecutionContext)
    extends Actor
    with ActorLogging {

  def createLotRegistrarActor(name: String): ActorRef =
    context.actorOf(LotRegistrarActor.props(name), name)

  def createLot(identifier: String, count: Int): Unit = {

    val registrarActor: ActorRef = createLotRegistrarActor(identifier)

    val newParticipants: Vector[Participant] = (1 to count).map { participantId =>
      Participant(s"participant #${participantId}")
    }.toVector

    registrarActor ! AddParticipant(newParticipants)
    sender() ! LotCreated(Lot(identifier, count))

  }

  override def receive: Receive = {

    case createClause @ CreateLot(identifier, count) =>
      log.debug(s"LotInformationActor: received create clause $createClause")
      context.child(identifier).fold(createLot(identifier, count))(_ => sender() ! LotExists)

    case removeClause @ RemoveParticipantsInfo(identifier, count) =>
      log.debug(s"LotInformationActor: received remove clause $removeClause")

      def notFound(): Unit = sender() ! Participants(identifier)
      def remove(child: ActorRef): Unit = child.forward(RemoveParticipants(count))

      context.child(identifier).fold(notFound())(remove)

    case lotInfoClause @ GetLotInfo(identifier) =>
      log.debug(s"LotInformationActor: received lot info clause $lotInfoClause")

      def notFound(): Unit = sender() ! None
      def forwardLot(child: ActorRef): Unit = child.forward(GetLot)

      context.child(identifier).fold(notFound())(forwardLot)

    case GetLots =>
      log.debug(s"LotInformationActor: received all lots info clause")

      def getLots: Iterable[Future[Option[Lot]]] =
        context.children.map { child =>
          self.ask(GetLotInfo(child.path.name)).mapTo[Option[Lot]]
        }

      def convertToLots(inputFuture: Future[Iterable[Option[Lot]]]): Future[Lots] =
        inputFuture.map(_.flatten).map(elem => Lots(elem.toVector))

      pipe(convertToLots(Future.sequence(getLots))) to sender()

    case cancelClause @ CancelLot(identifier) =>
      log.debug(s"LotInformationActor: received lot cancel clause $cancelClause")

      def notFound(): Unit = sender() ! None
      def cancelLot(child: ActorRef): Unit = child.forward(Cancel)

      context.child(identifier).fold(notFound())(cancelLot)

  }
}
