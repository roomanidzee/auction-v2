package com.romanidze.auction.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.romanidze.auction.entities.LotInformation
import com.romanidze.auction.entities.LotRegistrar
import com.romanidze.auction.utils.StopSystemAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class LotInformationActorSpec
    extends TestKit(ActorSystem("lot_information_actor_spec"))
    with AnyWordSpecLike
    with Matchers
    with ImplicitSender
    with StopSystemAfterAll {

  "LotInformationActor" should {

    import LotInformation._
    import LotRegistrar._

    "create lot and work with participants" in {

      implicit val timeout: Timeout = new Timeout(FiniteDuration(1000, TimeUnit.SECONDS))
      implicit val ec: ExecutionContext =
        ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

      val lotInformationActor: ActorRef = system.actorOf(LotInformationActor.props)
      val lot: String = "test_lot_1"

      lotInformationActor ! CreateLot(lot, 10)
      expectMsg(LotCreated(Lot(lot, 10)))

      lotInformationActor ! GetLots
      expectMsg(Lots(Vector(Lot(lot, 10))))

      lotInformationActor ! GetLotInfo(lot)
      expectMsg(Some(Lot(lot, 10)))

      lotInformationActor ! RemoveParticipantsInfo(lot, 1)
      expectMsg(Participants(lot, Vector(Participant("participant #1"))))

      lotInformationActor ! RemoveParticipantsInfo("DavidBowie", 1)
      expectMsg(Participants("DavidBowie"))

    }

  }

}
