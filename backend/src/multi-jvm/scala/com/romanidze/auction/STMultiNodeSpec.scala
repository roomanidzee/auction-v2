package com.romanidze.auction

import akka.remote.testkit.MultiNodeSpecCallbacks
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

trait STMultiNodeSpec extends MultiNodeSpecCallbacks
  with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = multiNodeSpecBeforeAll()

  override def afterAll(): Unit = multiNodeSpecAfterAll()
}

