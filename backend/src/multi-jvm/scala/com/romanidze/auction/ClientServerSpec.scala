package com.romanidze.auction

import akka.actor.{ActorIdentity, ActorPath, ActorSelection, Identify}
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import akka.util.Timeout
import com.romanidze.auction.actors.LotInformationActor
import com.romanidze.auction.entities.LotInformation
import com.romanidze.auction.entities.LotRegistrar

import scala.concurrent.duration._

class ClientServerSpecMultiJvmFrontend extends ClientServerSpec
class ClientServerSpecMultiJvmBackend extends ClientServerSpec

class ClientServerSpec extends MultiNodeSpec(MultiJVMConfig) with STMultiNodeSpec with ImplicitSender {

  import MultiJVMConfig._

  val backendNode: ActorPath = node(backend)

  override def initialParticipants: Int = roles.size

  "Auction Remote App" should {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "be able to create a lot and remove participants" in {

      runOn(backend) {
        system.actorOf(
          LotInformationActor.props(Timeout(1.second), system.dispatcher),
          LotInformationActor.name
        )
        enterBarrier("deployed")
      }

      runOn(frontend) {

        enterBarrier("deployed")

        val path: ActorPath = node(backend) / "user" / LotInformationActor.name
        val actorSelection: ActorSelection = system.actorSelection(path)

        actorSelection.tell(Identify(path), testActor)

        val actorRef = expectMsgPF() {
          case ActorIdentity(`path`, Some(ref)) => ref
        }

        import LotInformation._
        import LotRegistrar._

        actorRef ! CreateLot("RHCP", 20000)
        expectMsg(LotCreated(Lot("RHCP", 20000)))

        actorRef ! RemoveParticipantsInfo("RHCP", 1)
        expectMsg(Participants("RHCP", Vector(Participant("participant #1"))))

      }

      enterBarrier("finished")

    }

  }

}
