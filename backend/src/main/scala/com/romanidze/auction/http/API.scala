package com.romanidze.auction.http

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.romanidze.auction.actors.LotInformationActor

import scala.concurrent.ExecutionContextExecutor

class API(system: ActorSystem, timeout: Timeout) extends LotInformationRoutes {

  implicit override val requestTimeout: Timeout = timeout
  implicit override def executionContext: ExecutionContextExecutor = system.dispatcher

  override def createLotInformationActor: ActorRef =
    system.actorOf(LotInformationActor.props, "auction_lot_information")
}
