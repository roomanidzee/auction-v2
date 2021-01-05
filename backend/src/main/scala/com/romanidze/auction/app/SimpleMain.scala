package com.romanidze.auction.app

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import com.romanidze.auction.actors.LotInformationActor
import com.romanidze.auction.http.API
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext

object SimpleMain extends App with Launcher {

  val config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem("auction-actor-system", config)

  val api = new API() {

    override def createLotInformationActor: ActorRef =
      system.actorOf(LotInformationActor.props, LotInformationActor.name)

    implicit override def executionContext: ExecutionContext = system.dispatcher

    implicit override def requestTimeout: Timeout = configureTimeout(config)
  }

  start(api.routes)

}
