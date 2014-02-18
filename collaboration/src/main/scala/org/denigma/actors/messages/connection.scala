package org.denigma.actors.messages

import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.iteratee.Concurrent._
import java.util.{Date, Calendar}
import akka.actor.ActorRef



case class CannotConnect(msg: String)

case class Connected(name:String,user:ActorRef,enum:Enumerator[JsValue])

case class Join(username:String, email:String, token:String, hash:String)

case class Start(channel:Channel[JsValue], connection:String = "default")

case class Quit(username: String, connection:String)









