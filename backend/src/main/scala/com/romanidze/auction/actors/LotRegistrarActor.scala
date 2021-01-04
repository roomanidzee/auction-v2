package com.romanidze.auction.actors

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.romanidze.auction.entities.LotRegistrar._
import com.romanidze.auction.entities.LotInformation.Lot

object LotRegistrarActor {

  case class State(participants: Vector[Participant] = Vector.empty[Participant])

  def props(identifier: String): Props = Props(new LotRegistrarActor(identifier))

}

class LotRegistrarActor(identifier: String) extends Actor with ActorLogging {

  import LotRegistrarActor._

  override def preStart(): Unit =
    log.info("LogRegistrarActor: going to preStart phase")

  override def postStop(): Unit = log.info("LogRegistrarActor: going to postStop phase")

  override def receive: Receive = process(State())

  private def process(state: State): Receive = {

    case addClause @ AddParticipant(participants) =>
      log.debug(s"LogRegistrarActor: Received add clause: $addClause")
      context.become(process(state.copy(state.participants ++ participants)))

    case removeClause @ RemoveParticipants(count) =>
      log.debug(s"LogRegistrarActor: Received remove clause: $removeClause")

      val entries: Vector[Participant] = state.participants.take(count)

      if (entries.size >= count) {
        sender() ! Participants(identifier, entries)
        context.become(process(state.copy(state.participants.drop(count))))
      } else {
        sender() ! Participants(identifier)
      }

    case GetLot =>
      log.debug("LogRegistrarActor: Received Lot getter clause")
      sender() ! Some(Lot(identifier, state.participants.size))

    case Cancel =>
      log.debug("LogRegistrarActor: Received Lot cancel clause")
      sender() ! Some(Lot(identifier, state.participants.size))
      self ! PoisonPill

  }

}
