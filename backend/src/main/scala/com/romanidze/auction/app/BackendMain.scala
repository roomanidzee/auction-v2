package com.romanidze.auction.app

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object BackendMain extends App {

  val config = ConfigFactory.load("backend")
  val system = ActorSystem("backend", config)

  Await.result(system.whenTerminated, Duration.Inf)

}
