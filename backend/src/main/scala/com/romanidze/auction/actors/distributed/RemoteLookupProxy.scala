package com.romanidze.auction.actors.distributed

import akka.actor.{
  Actor,
  ActorIdentity,
  ActorLogging,
  ActorRef,
  Identify,
  ReceiveTimeout,
  Stash,
  Terminated
}

import scala.concurrent.duration._

// просто пример, который не используется в проекте - нужен для изучения
class RemoteLookupProxy(path: String) extends Actor with Stash with ActorLogging {

  val logPrefix = "RemoteLookupProxy:"

  context.setReceiveTimeout(3.seconds)
  sendIdentifyRequest()

  def sendIdentifyRequest(): Unit = {

    val selection = context.actorSelection(path)
    selection ! Identify(path)

  }

  override def receive: Receive = process

  def process: Receive = {

    case ActorIdentity(actorPath, Some(actorValue)) =>
      context.setReceiveTimeout(Duration.Undefined)
      log.info(s"$logPrefix switching $actorPath to active state")
      context.become(checkActive(actorValue))
      context.watch(actorValue)
      unstashAll()

    case ActorIdentity(actorPath, None) =>
      log.error(s"$logPrefix Remote actor with path $actorPath is not available.")

    case ReceiveTimeout =>
      sendIdentifyRequest()

    case msg: Any =>
      log.warning(s"$logPrefix Stashing message $msg, remote actor is not ready yet.")
      stash()

  }

  def checkActive(actor: ActorRef): Receive = {

    case Terminated(actorRef) =>
      log.info(s"$logPrefix Actor $actorRef terminated")
      log.info(s"$logPrefix switching $actor to identify state")

      context.become(process)
      context.setReceiveTimeout(3.seconds)
      sendIdentifyRequest()

    case msg: Any => actor.forward(msg)

  }

}
