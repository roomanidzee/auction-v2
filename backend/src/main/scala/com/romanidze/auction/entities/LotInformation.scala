package com.romanidze.auction.entities

object LotInformation {

  case class Lot(identifier: String, count: Int)
  case class Lots(input: Vector[Lot])

  case class CreateLot(identifier: String, count: Int)
  case class GetLotInfo(identifier: String)
  case object GetLots

  case class RemoveParticipantsInfo(identifier: String, count: Int)
  case class CancelLot(identifier: String)

  sealed trait LotResponse
  case class LotCreated(event: Lot) extends LotResponse
  case object LotExists extends LotResponse

}
