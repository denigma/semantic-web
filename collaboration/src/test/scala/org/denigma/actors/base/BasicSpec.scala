package org.denigma.actors.base

import akka.actor.ActorSystem
import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.json.JsValue



import akka.pattern.ask


import play.api.libs.iteratee._

import scala.concurrent.duration._

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers


import akka.testkit._

import akka.actor._
import org.denigma.actors._
import org.denigma.actors.messages._
import org.denigma.actors.models._
import scala.util.Success
import play.api.libs.json._
import  scala.language.postfixOps
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import org.denigma.actors.mock.MockMaster
import org.denigma.actors.rooms.messages.{RoomMates, Tell}
import org.denigma.actors.rooms.RoomActor
import java.util.Date
import scala.concurrent.{ ExecutionContext, Promise }
//import play.api.libs.concurrent.Execution.Implicits._

class BasicSpec(_system: ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{

  implicit val sys =_system
  implicit val timeout = akka.util.Timeout(1 second)
  implicit def context = sys.dispatcher



  def makeProbes(channels: Vector[(Iteratee[JsValue, _], Enumerator[JsValue])]) = channels.map{
    case (input,output)=>
      val probe = TestProbe()
      output(Iteratee.foreach[JsValue] { event => probe.ref  ! event })
      probe
  }


  var channels = Vector.empty[(Iteratee[JsValue, _], Enumerator[JsValue])]

  val (user1, password1) = ("firstUser","firstPassword")
  val (user2, password2) = ("secondUser","secondPassword")
  val (user3, password3) = ("thirdUser","thirdPassword")

  override def afterAll {
    system.shutdown()
  }

  /**
   * Extracts channels from connection
   * @param username
   * @param con
   * @param now
   * @return
   */
  def extractChannels(username:String,con:Connected)(implicit now:Date): (Iteratee[JsValue, Unit], Enumerator[JsValue]) = {
    val input = Iteratee.foreach[JsValue] {
      (event: JsValue) =>
        con.user ! Received(now, Json.toJson(event), con.name)
    }
      .mapDone {
      _ => self ! Quit(username, con.name)
    }
    (input,con.enum)
  }


}