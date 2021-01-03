package com.romanidze.auction.entities

import spray.json._

object LotMarshalling {
  case class LotDescription(count: Int)

  case class ParticipantRequest(count: Int)

  case class ErrorInfo(message: String)
}

trait LotMarshalling extends DefaultJsonProtocol {

  import LotInformation._
  import LotRegistrar._
  import LotMarshalling._

  implicit val lotDescriptionFormat: RootJsonFormat[LotDescription] = jsonFormat1(LotDescription)

  implicit val lotFormat: RootJsonFormat[Lot] = jsonFormat2(Lot)
  implicit val lotsFormat: RootJsonFormat[Lots] = jsonFormat1(Lots)

  implicit val participantRequestFormat: RootJsonFormat[ParticipantRequest] = jsonFormat1(
    ParticipantRequest
  )
  implicit val participantFormat: RootJsonFormat[Participant] = jsonFormat1(Participant)
  implicit val participantsFormat: RootJsonFormat[Participants] = jsonFormat2(Participants)

  implicit val errorInfoFormat: RootJsonFormat[ErrorInfo] = jsonFormat1(ErrorInfo)

}
