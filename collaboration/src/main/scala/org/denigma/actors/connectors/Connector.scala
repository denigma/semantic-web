package org.denigma.actors.connectors

import akka.actor._
import scala.concurrent.duration._

import play.api.libs.concurrent._


import play.api.libs.iteratee._
import play.api.Play.current
import org.denigma.actors._
import java.util.{Calendar, Date}
import akka.util._
import scala.language.postfixOps
import scala.reflect.ClassTag
import org.denigma.actors.staff.{NamedActor, Creator}
import play.api.libs.json.JsValue


/**
 * Basic trait to connect to websocket
 */
abstract class Connector[TI,TE, TMainActor<:NamedActor: ClassTag] extends Creator
{
  implicit val timeout = akka.util.Timeout(1 second)
  implicit def now: Date = Calendar.getInstance().getTime()
  var currentUser:ActorRef = null


  lazy val master= this.createActor[TMainActor]("MASTER")//Akka.system.actorOf(Props[Master],"MASTER")

  /*
  /**
   * Abstract connection method
   * @param username user who connects
   * @param password user's password
   * @return
   */
  def join(username:String,password:String):scala.concurrent.Future[(Iteratee[TI,_],Enumerator[TE])]
    */

  def join(username:String, email:String, token:String, hash:String): scala.concurrent.Future[(Iteratee[JsValue,_],Enumerator[JsValue])]

}
