package com.romanidze.auction.app

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import com.romanidze.auction.http.API
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Launcher extends App with RequestTimeout {

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system: ActorSystem = ActorSystem("auction-actor-system")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val api: Route = new API(system, requestTimeout(config)).routes

  val bindingFuture: Future[ServerBinding] = Http().newServerAt(host, port).bind(api)

  val log = Logging(system.eventStream, "auction")

  bindingFuture.onComplete {

    case Success(value) =>
      log.info("Server launched at http://{}:{}/",
               value.localAddress.getHostString,
               value.localAddress.getPort
      )

    case Failure(exception) =>
      log.error("Server didn't start! Reason: {}", exception)
      exception.printStackTrace()
      system.terminate()

  }

  Await.result(system.whenTerminated, Duration.Inf)

}
