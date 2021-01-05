package com.romanidze.auction.app

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.romanidze.auction.actors.distributed.LotInformationForwarderActor
import com.romanidze.auction.app.SimpleMain.{configureTimeout, start}
import com.romanidze.auction.http.API
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

object FrontendMain extends App {

  val config = ConfigFactory.load("frontend")
  implicit val system = ActorSystem("frontend", config)

  val api = new API() {

    override def createLotInformationActor: ActorRef =
      system.actorOf(LotInformationForwarderActor.props, LotInformationForwarderActor.name)

    implicit override def executionContext: ExecutionContext = system.dispatcher

    implicit override def requestTimeout: Timeout = configureTimeout(config)
  }

  start(api.routes)

}
