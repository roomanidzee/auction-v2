package com.romanidze.auction.entities

object LotRegistrar {

  case class Participant(identifier: String)
  case class Participants(lotTitle: String,
                          participants: Vector[Participant] = Vector.empty[Participant]
  )

  case class AddParticipant(participants: Vector[Participant])
  case class RemoveParticipants(count: Int)

  case object GetLot
  case object Cancel

}
