package org.denigma.actors.workers

import play.api.libs.json._
import play.api.libs.iteratee.Concurrent._


import org.denigma.actors.messages._
import akka.util.Timeout
import play.api.libs.concurrent.Akka
import concurrent.ExecutionContext
import play.api.Play.current
import scala.concurrent.duration._
import org.denigma.actors.Member
import org.denigma.actors.rooms.messages.TellOthers


class Robot extends Member {
  //implicit val executionContext:ExecutionContext = Akka.system.dispatcher

  //val robot = context.actorOf(Props[Robot],name = "robot")
  //override implicit val timeout = Timeout(1 seconds)
  Akka.system.scheduler.schedule(
    5 seconds,
    5 seconds,
    parent,
    TellOthers(name,hiText)
  )

  def alive() = {
    this.parent ! TellOthers(name, aliveText)
  }

  def hi() = {
    this.parent ! TellOthers(name, hiText)
  }

  def aliveText:JsValue = Json.obj(
    "channel" -> "messages",
    "content" -> Json.obj(
      "id" -> makeId,
      "user" -> "Robot",
      "text" -> s"I am stilll alive! My id is ${makeId}"
    ),
    "request"->"push")

  def hiText:JsValue = Json.obj(
    "channel" -> "messages",
    "content" -> Json.obj(
      "id" -> makeId,
      "user" -> "Robot",
      "text" -> "Hi guys! it is a FEEDBACK!"
    ),
    "request"->"push")


}
