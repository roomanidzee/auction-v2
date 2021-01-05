package com.romanidze.auction.app

import akka.actor.{ActorSystem, Terminated}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import com.romanidze.auction.utils.RequestTimeout
import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

trait Launcher extends RequestTimeout {

  def start(api: Route)(implicit system: ActorSystem): Terminated = {

    val config: Config = system.settings.config

    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    startServer(api, host, port)

  }

  def startServer(api: Route, host: String, port: Int)(implicit system: ActorSystem): Terminated = {

    implicit val ec: ExecutionContextExecutor = system.dispatcher

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

}
