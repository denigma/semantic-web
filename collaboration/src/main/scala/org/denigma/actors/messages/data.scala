package org.denigma.actors.messages

import scala.reflect.runtime.universe._

import java.util.{Date, Calendar}
import play.api.libs.json.JsValue
import org.denigma.actors.models._
import akka.actor.{ActorRef, Actor}


/**
 * Used to ask history from Cache actor
 * @param date
 * @param to
 * @param query
 */
case class History(date:Date,  to:String="sender", query:String="all") extends DataLike

case class Data[T](date:Date,value:T) extends DatedValueLike[T]

//case class Output(value:JsValue) extends ValueLike[JsValue]

/**
 * Used to write received info
 * @param date
 * @param value
 * @tparam T type of the value
 */
case class Received[T](date:Date,value: T, connection:String = "default") extends DatedValueLike[T]

/**
 * Used to send info to the client, it will be parsed to JSON in case it is not JsValue
 * @param date
 * @param value
 * @tparam T type of the value
 */
case class Push[T](date:Date,value:T) extends DatedValueLike[T]

/**
 * Removes
 * @param id id of thing to be removed from the client
 * channel where the info will be removed
 */
case class Delete(id:String, channel:String)







