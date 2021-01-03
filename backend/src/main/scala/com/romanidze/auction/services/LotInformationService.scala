package com.romanidze.auction.services

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.romanidze.auction.entities.{LotInformation, LotRegistrar}

import scala.concurrent.{ExecutionContext, Future}

trait LotInformationService {

  import LotInformation._
  import LotRegistrar._

  def createLotInformationActor: ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val lotInformationActor: ActorRef = createLotInformationActor

  def createLot(identifier: String, count: Int): Future[LotResponse] =
    lotInformationActor.ask(CreateLot(identifier, count)).mapTo[LotResponse]

  def retrieveLots(): Future[Lots] = lotInformationActor.ask(GetLots).mapTo[Lots]

  def retrieveLot(identifier: String): Future[Option[Lot]] =
    lotInformationActor.ask(GetLotInfo(identifier)).mapTo[Option[Lot]]

  def cancelLot(identifier: String): Future[Option[Lot]] =
    lotInformationActor.ask(CancelLot(identifier)).mapTo[Option[Lot]]

  def removeParticipants(lotIdentifier: String, count: Int): Future[Participants] =
    lotInformationActor.ask(RemoveParticipantsInfo(lotIdentifier, count)).mapTo[Participants]

}
