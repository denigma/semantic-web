package org.denigma.actors

import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Concurrent.Channel
import akka.pattern._
import org.scalatest._
import org.scalatest.matchers.ShouldMatchers


import scala.concurrent.duration._


import akka.actor._

import play.api.libs.json._
import play.api.libs.iteratee.{Enumerator, Iteratee}

import akka.testkit._
import  scala.language.postfixOps

import scala.util.Success
import org.denigma.actors.messages._

import org.denigma.actors.models._
import org.denigma.actors.mock.MockMaster
import org.denigma.actors.rooms.RoomActor
import org.denigma.actors.rooms.messages.JoinRoom
import org.denigma.actors.base.{MainSpec, BasicSpec}


/**
 * Test spec to test member actor
 * */
class MemberSpec(_system: ActorSystem) extends MainSpec[MockMaster](_system)// extends BasicSpec(_system)
//extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{

  def this() = this(ActorSystem("MySpec"))


  "Member actor" should{

    addUser(user1,password1)
    addUser(user2,password2)
    addUser(user3,password3)


    val(Some(u1),Some(u2),Some(u3)) =
      (masterActor.testMembers.get(user1),masterActor.testMembers.get(user2),masterActor.testMembers.get(user3))
    val (ua1,ua2,ua3) = (u1.underlyingActor,u2.underlyingActor,u3.underlyingActor)

    val d= ua1.now



   val lcmess = "My message is very long and complicated"

    val lmess =  "My message is very long"

    val someId = "someID"


    def mq() =  ua1.messages.count(r=>r.value.id==someId)



    val val1:JsValue = Json.obj(
      "channel" -> "messages",
      "content" -> Json.obj(
        "id" -> someId,
        "user" -> "bob",
        "text" -> lmess
      ),
      "request"->"save", "room"->"all")


    val val1Upd:JsValue = Json.obj(
      "channel" -> "messages",
      "content" -> Json.obj(
        "id" -> someId,
        "user" -> "bob",
        "text" -> lcmess
      ),
      "request"->"save", "room"->"all")


    val val2:JsValue = Json.obj(
      "channel" -> "messages",
      "content" -> Json.obj(
        "id" -> "someID2",
        "user" -> "bob",
        "text" -> lmess
      ),
      "request"->"save", "room"->"all")

    val valR:JsValue = Json.obj(
      "channel" -> "messages",
      "content" -> Json.obj(
        "id" -> someId
      ),
      "request"->"remove", "room"->"all"
    )


    "receive and parseOperations messages" in {
      val (input, output) = channels(0)
      //create enumerator to push value into the actor
      val (enum: Enumerator[JsValue],channel: Channel[JsValue]) = Concurrent.broadcast[JsValue]
      enum(input)


      val Some(user) = master.underlyingActor.testMembers.get(user1)
      val userActor = user.underlyingActor
      userActor.messages.size.shouldEqual(0)
      //user ! Received(value)
      channel.push(val1)
    }

    "send messages" in {
      val probes = makeProbes(channels)
      u1 ! Push(ua1.now,val1)
      probes(0).expectMsg(200 millis,val1)
      probes(0).expectNoMsg(200 millis)
      probes(0).expectNoMsg(200 millis)
    }



    "update" in {


      mq()  shouldEqual 1
      ua1.getWithout(ua1.messages,"someID2").count(r=>r.value.id==someId).shouldEqual(1)
      ua1.getWithout(ua1.messages,someId).count(r=>r.value.id==someId).shouldEqual(0)
      mq()  shouldEqual 1

      val s = ua1.messages.size

      ua1.receive(Received(d,val1))

      mq()  shouldEqual 1

      ua1.messages.size.shouldEqual(s)

      ua1.receive(Received(d,val1))

      mq()  shouldEqual 1

      ua1.messages.size.shouldEqual(s)


      (val2 \ "content" \ "id").as[String].shouldEqual("someID2")

      ua1.receive(Received(d,val2))

      ua1.messages.size.shouldEqual(2)

      mq shouldEqual 1

      val Some(m1) = ua1.messages.find(m=>m.value.id==someId)
      m1.value.text.shouldEqual(lmess)

      ua1.receive(Received(d,val1Upd))

      val Some(m2) =  ua1.messages.find(m=>m.value.id==someId)
      m2.value.text.shouldEqual(lcmess)

      mq()  shouldEqual 1
    }

    "remove messages" in {
      ua1.messages.size.shouldEqual(2)
      ua1.receive(Received(d,valR))
      ua1.messages.size.shouldEqual(1)
      mq() shouldEqual 0

    }

  }



  "Parsers inside member actor" should{
    val(Some(u1),Some(u2),Some(u3)) =
      (masterActor.testMembers.get(user1),masterActor.testMembers.get(user2),masterActor.testMembers.get(user3))
    val (ua1,ua2,ua3) = (u1.underlyingActor,u2.underlyingActor,u3.underlyingActor)
     "pack messages into requestsInfo" in{
       val mess = Message("someId","someUser","someText")
       val req: RequestInfo = ua1.pack2RequestInfo(mess,"push")
       req.channel.shouldEqual("messages")
       (req.content \ "id").as[String].shouldEqual("someId")
     }

    "pack messages into requests" in{
      val mess = Message("someId","someUser","someText")
      val req: JsValue = ua1.pack2Request(mess,"push")
      (req \ "channel").as[String].shouldEqual("messages")
      (req \ "content" \ "id").as[String].shouldEqual("someId")

    }

  }

  "Member" should
   {
     val newUser = "newUser"
       "work with rooms" in {

         this.addUser(newUser,password1)
         val Some(nu) = masterActor.testMembers.get(newUser)
         val nua = nu.underlyingActor
         nua.rooms.size.shouldEqual(1)
         val nr = "newRoom"
         val tr = masterActor.addTestRoom[RoomActor](nr)
         masterActor.rooms.size.shouldEqual(2)
         nua.rooms.size.shouldEqual(1)
         tr.underlyingActor.members.size.shouldEqual(0)
         nu ! JoinRoom(newUser,tr)
         nua.rooms.size.shouldEqual(2)




       }
   }



}
