package com.romanidze.auction

import akka.remote.testconductor.RoleName
import akka.remote.testkit.MultiNodeConfig

object MultiJVMConfig extends MultiNodeConfig{

  val frontend: RoleName = role("frontend")
  val backend: RoleName = role("backend")

}
