package org.denigma.video


import scala.concurrent.duration._


import akka.testkit._

import akka.actor._
import play.api.libs.json._
import  scala.language.postfixOps


import scalax.collection.mutable.{Graph => MGraph}
import org.denigma.actors.messages._
import org.denigma.video.managers.BindingManager
import org.denigma.actors.models.RequestInfo
import org.denigma.video.mock.MockVideoMaster
import org.denigma.video.messages.CloseBinding
import org.denigma.video.messages.OpenBinding
import scala.Some
import org.denigma.video.messages.ChangeBinding
import org.denigma.actors.base.MainSpec


/**
 * Test spec for video
 * @param _system test actorsystem
 *
 */
class VideoMemberSpec(_system: ActorSystem)  extends MainSpec[MockVideoMaster](_system)// extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{


  def this() = this(ActorSystem("MySpec"))

  this.addUser(user1,password1)
  this.addUser(user2,password2)
  this.addUser(user3,password3)

  val(Some(u1),Some(u2),Some(u3)) =
    (masterActor.testMembers.get(user1),masterActor.testMembers.get(user2),masterActor.testMembers.get(user3))
  val (ua1,ua2,ua3) = (u1.underlyingActor,u2.underlyingActor,u3.underlyingActor)



  "Binding in VideoMember" should {



    val func:  BindingManager.ChannelRequestRoomContentDateChecker = (channel,request,room,content)=>true

    val probe = new TestProbe(this._system)
    val lookContent = Json.obj(
      "query" -> "50",
      "field" -> "genes")

    val look:JsValue = Json.obj(
      "channel"->"genes",
      "content" -> lookContent,
      "request"->"lookup",
      "room"->"all"
    )


    "be able to open" in {


      ua1.bindings.size.shouldEqual(0)

      ua1.receive(OpenBinding("name",func,probe.ref))
      probe.expectMsg[ChangeBinding](200 millis,ChangeBinding(user1,"opened",u1))
    }

    "be able to work" in {

      ua1.bindings.size.shouldEqual(1)
      ua1.bindings.keys.head.shouldEqual(probe.ref)
      //ua1.receive(rec)
      ua1.parse(look)(ua1.now)

      //ua1.parseBinding("some","some","other",look, masterActor.now)
      //probe.expectMsg(200 millis,rec)
      probe.receiveN(1,200 millis)
    }

    "be able to close" in {

      ua1.receive(CloseBinding("name",probe.ref))
      ua1.bindings.size.shouldEqual(0)
      ua1.parse(look)(ua1.now)
      probe.receiveN(1,200 millis)

    }
  }

  "Video Manager" should
    {

      val probes = this.makeProbes(channels)


      val iceContent = Json.obj("someICE" -> "someICE")

      val ice:JsValue = Json.obj(
        "channel" -> "ICE",
        "content" -> iceContent,
        "request"->"ICE", "room"->user2)

      val iceReq = RequestInfo("ICE",iceContent,"ICE",user2)


      val offer:JsValue = Json.obj(
        "channel" -> "Slot1",
        "content" -> iceContent,
        "request"->"offer", "room"->user3)

      val offerReq = RequestInfo("Slot1",iceContent,"offer",user3)


      val answer:JsValue = Json.obj(
        "channel" -> "SomeAnswer",
        "content" -> Json.obj("someICE" -> "someICE"),
        "request"->"answer", "room"->user2)

      val stream:JsValue = Json.obj(
        "channel" -> "SomeStream",
        "content" -> Json.obj("someICE" -> "someICE"),
        "request"->"stream", "room"->user2)

    "work with ICE" in
    {
        u1 ! Received(ua1.now,ice)
        probes(0).expectNoMsg(200 millis)
        probes(1).receiveN(1,200 millis)
        probes(2).expectNoMsg(200 millis)
    }

    "work with offer" in
      {
        u1 !  Received(ua1.now,offer)
        probes(0).expectNoMsg(200 millis)
        probes(1).expectNoMsg(200 millis)
        probes(2).receiveN(1,200 millis)

      }
  }

}
