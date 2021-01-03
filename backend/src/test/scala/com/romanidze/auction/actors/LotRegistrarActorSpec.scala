package com.romanidze.auction.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.romanidze.auction.entities.LotRegistrar
import com.romanidze.auction.utils.StopSystemAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class LotRegistrarActorSpec
    extends TestKit(ActorSystem("lot_registrar_actor_spec"))
    with AnyWordSpecLike
    with Matchers
    with ImplicitSender
    with StopSystemAfterAll {

  "LotRegistrarActor" should {

    "add and remove participants" in {

      import LotRegistrar._

      val participants: Vector[Participant] =
        (1 to 10).map(elem => Participant(s"participant #$elem")).toVector
      val lot: String = "test_lot"
      val lotRegistrarActor = system.actorOf(LotRegistrarActor.props(lot))

      lotRegistrarActor ! AddParticipant(participants)
      lotRegistrarActor ! RemoveParticipants(1)

      expectMsg(Participants(lot, Vector(Participant("participant #1"))))

    }

  }

}
