package com.romanidze.auction.utils

import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.duration.{Duration, FiniteDuration}

trait RequestTimeout {

  def configureTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }

}
