package com.romanidze.auction.actors.distributed

import akka.actor.{Actor, ActorLogging, ActorRef, Props, ReceiveTimeout, Terminated}
import akka.util.Timeout
import com.romanidze.auction.actors.LotInformationActor

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object LotInformationForwarderActor {

  def props(implicit timeout: Timeout, executor: ExecutionContext): Props =
    Props(new LotInformationForwarderActor)

  def name: String = "forwarder"

}

class LotInformationForwarderActor(implicit timeout: Timeout, executor: ExecutionContext)
    extends Actor
    with ActorLogging {

  val logPrefix = "LotInformationForwarderActor: "

  context.setReceiveTimeout(3.seconds)
  startActor()

  def startActor(): Unit = {

    val actor: ActorRef = context.actorOf(LotInformationActor.props, LotInformationActor.name)
    context.watch(actor)

    log.info(s"$logPrefix switching actor $actor to maybe active state")

  }

  override def receive: Receive = process

  def process: Receive = {

    case ReceiveTimeout => startActor()

    case msg: Any =>
      log.error(s"$logPrefix Ignoring message $msg, remote actor is not ready yet.")

  }

  def checkActive(actor: ActorRef): Receive = {

    case Terminated(actorRef) =>
      log.info(s"$logPrefix Actor $actorRef terminated")
      log.info(s"$logPrefix switching $actor to deploying state")
      context.become(process)

      context.setReceiveTimeout(3.seconds)
      startActor()

    case msg: Any => actor.forward(msg)

  }

}
