package org.denigma.actors

import scala.Predef._


import akka.pattern.ask


import play.api.libs.iteratee._

import scala.concurrent.duration._

import org.scalatest._


import akka.testkit._

import akka.actor._
import org.denigma.actors.messages._
import org.denigma.actors.models._
import scala.util.Success
import play.api.libs.json._
import  scala.language.postfixOps
import org.denigma.actors.mock.MockMaster
import org.denigma.actors.workers.Robot
import org.denigma.actors.rooms.messages.{Tell, TellOthers}
import org.denigma.actors.base.{MainSpec, BasicSpec}
import scala.concurrent.Await

import akka.util.Timeout
/**
 * test spec to test master actor
 * */
class MasterSpec(_system: ActorSystem) extends BasicSpec(_system)  //extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{
  def this() = this(ActorSystem("MySpec"))

 "Master actor" should
 {
    val master = TestActorRef[MockMaster]
    val masterActor = master.underlyingActor

    var channels = Vector.empty[(Iteratee[JsValue, _], Enumerator[JsValue])]

    val val1:JsValue = Json.obj(
     "channel" -> "messages",
     "content" -> Json.obj(
       "id" -> "someID",
       "user" -> "bob",
       "text" -> "My message is very long"
     ),
     "request"->"save", "room"->"all")

   val all = master.underlyingActor.defTestRoom

   "check actor names" in {

    masterActor.allowName("").shouldEqual(false)
     masterActor.allowName(null).shouldEqual(false)
     masterActor.allowName("/--№98@aaa").shouldEqual(false)
     masterActor.allowName(user1).shouldEqual(true)
     masterActor.allowName(user2).shouldEqual(true)

   }

    "add first user" in {

      masterActor.members.shouldEqual(Map.empty[String,ActorRef])

      masterActor.testMembers.get(user1).shouldEqual(None)

      //val Success(ConnectedSimple(input,output)) = ( master ? SimpleJoin(user1,password1) ).mapTo[ConnectedSimple].value.get
      val fut = ( master ? Join(user1,"noemail","notoken",password1) ).mapTo[Connected]
      val con = Await.result[Connected](fut,this.timeout.duration)
      val chns = this.extractChannels(user1,con)(masterActor.now)


      masterActor.members should contain key user1
      masterActor.members should not contain key(user2)

      masterActor.testMembers should contain key user1
      masterActor.testMembers should not contain key(user2)

      channels = channels :+ chns
      channels(0).shouldEqual(chns)

      val user = masterActor.testMembers.get(user1).get
      user.underlyingActor.channels.size.shouldEqual(1)


    }

    "not allow to connect with wrong password" in {
      masterActor.authWorkerActor.users.length.shouldEqual(1)
      masterActor.authWorkerActor.users.head.username.shouldEqual(user1)
      masterActor.authWorkerActor.users.head.hash.shouldEqual(password1)


      val user = masterActor.testMembers.get(user1).get
      user.underlyingActor.channels.size.shouldEqual(1)

      //val Success(resultWrong) = (master ? SimpleJoin(user1, password1) ).value.get
      val fut = ( master ? Join(user1,"noemail","notoken","wrong password") ).mapTo[CannotConnect]
      val resultWrong = Await.result[CannotConnect](fut,this.timeout.duration)


      resultWrong.isInstanceOf[Connected].shouldEqual(false)
      resultWrong.isInstanceOf[CannotConnect].shouldEqual(true)

      user.underlyingActor.channels.size.shouldEqual(1)
      masterActor.authWorkerActor.users.length.shouldEqual(1)
      masterActor.authWorkerActor.users.head.hash.shouldEqual(password1)

    }

    "add user 2" in {

      masterActor.testMembers.get(user2).shouldEqual(None)

      val fut = ( master ? Join(user2,"noemail","notoken",password2) ).mapTo[Connected]
      val con = Await.result[Connected](fut,this.timeout.duration)
      val chns = this.extractChannels(user2,con)(masterActor.now)




      masterActor.members should contain key user1
      masterActor.members should contain key user2
      masterActor.members should not contain key(user3)

      channels = channels :+ chns
      channels(1).shouldEqual(chns)

      val user = masterActor.testMembers.get(user2).get
      user.underlyingActor.channels.size.shouldEqual(1)

    }

    "add user 3" in
      {

      masterActor.testMembers.get(user3).shouldEqual(None)

        val fut = ( master ? Join(user3,"noemal","notoken",password3) ).mapTo[Connected]
        val con = Await.result[Connected](fut,this.timeout.duration)
        val chns = this.extractChannels(user3,con)(masterActor.now)



        masterActor.testMembers should contain key user1
      masterActor.testMembers should contain key user2
      masterActor.testMembers should contain key user3

      val user = masterActor.testMembers.get(user3).get
      user.underlyingActor.channels.size.shouldEqual(1)


      channels = channels :+ chns
      channels(2).shouldEqual(chns)

    }

   "broadcast json to all users" in
   {
     val probes = makeProbes(channels)


     masterActor.rooms.size.shouldEqual(1)

     masterActor.defTestRoom.underlyingActor.members.size.shouldEqual(3)
     all.underlyingActor.members.size.shouldEqual(3)

     masterActor.defTestRoom.shouldEqual(all)

     //all.receive(Tell(val1))
     all.underlyingActor.tell(val1)

     //all ! Tell(val1)

     probes(0).receiveN(1,250 millis)
     probes(1).receiveN(1,250 millis)
     probes(2).receiveN(1,250 millis)


   }

   "broadcast messages to all users" in
   {
     val probes = makeProbes(channels)


     val(Some(u1),Some(u2),Some(u3)) =
       (masterActor.testMembers.get(user1),masterActor.testMembers.get(user2),masterActor.testMembers.get(user3))
     val (ua1,ua2,ua3) = (u1.underlyingActor,u2.underlyingActor,u3.underlyingActor)

     val mess = Message("someId","someUser","someText")
     masterActor.defTestRoom.underlyingActor.members.size.shouldEqual(3)
     all.underlyingActor.members.size.shouldEqual(3)


     all !  Tell(mess)

     probes(0).receiveN(1,200 millis)
     probes(1).receiveN(1,200 millis)
     probes(2).receiveN(1,200 millis)



   }

   "spread message from one user to others" in
   {
     val probes = makeProbes(channels)

     val mess = Message("someId","someUser","someText")

     masterActor.defTestRoom.underlyingActor.members.size.shouldEqual(3)

     all ! TellOthers[Message](user2,mess)

     probes(0).receiveN(1,200 millis)
     probes(1).expectNoMsg(200 millis)
     probes(2).receiveN(1,200 millis)

   }

   "support rooms" in
     {
       val probes = makeProbes(channels)

       val mess = Message("someId","someUser","someText")

       all ! TellOthers[Message](user2,mess)

       probes(0).receiveN(1,200 millis)
       probes(1).expectNoMsg(200 millis)
       probes(2).receiveN(1,200 millis)

     }

   "merge users" in {

     masterActor.authWorkerActor.users.length.shouldEqual(3)
     val join = masterActor.authWorkerActor.users(2)
     join.username.shouldEqual(user1)
     join.hash.shouldEqual(password1)

     val user = masterActor.testMembers.get(user1).get
     user.underlyingActor.channels.size.shouldEqual(1)


     val fut = ( master ?Join(user1,"noemail","notoken",password1) ).mapTo[Connected]
     val con = Await.result[Connected](fut,this.timeout.duration)



     masterActor.members should contain key user1

     masterActor.testMembers should contain key user1

     user.underlyingActor.channels.size.shouldEqual(2)

   }


  }
 "Bots " should
  {
    val master = TestActorRef[MockMaster]
    val masterActor = master.underlyingActor

    var channels = Vector.empty[(Iteratee[JsValue, _], Enumerator[JsValue])]

    val val1:JsValue = Json.obj(
      "channel" -> "messages",
      "content" -> Json.obj(
        "id" -> "someID",
        "user" -> "bob",
        "text" -> "My message is very long"
      ),
      "request"->"save")

    //TODO: rewrite robot creation part
    /*
    def makeRobot() = masterActor.createTestActor[Robot]("robot")//new Robot()


    "be added together with users" in
    {
      master ! Bot("robot",makeRobot)
      val last = masterActor.members.last
      last._1.shouldEqual("robot")
    }
    */

  }

}
