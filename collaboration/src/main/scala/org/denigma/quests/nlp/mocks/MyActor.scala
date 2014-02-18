package org.denigma.semantic.query.mocks

import akka._
import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging

/**
 * Created with IntelliJ IDEA.
 * Member: AntonKulaga
 * Date: 1/22/13
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
class MyActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case "test" ⇒ log.info("received test")
    case _      ⇒ log.info("received unknown message")
  }
}