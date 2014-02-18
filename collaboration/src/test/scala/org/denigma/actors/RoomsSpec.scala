package org.denigma.actors

import scala.Predef._


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
import org.denigma.actors.base.{MainSpec, BasicSpec}

class RoomsSpec(_system: ActorSystem)  extends MainSpec[MockMaster](_system)//extends BasicSpec(_system)
  //extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{
  def this() = this(ActorSystem("MySpec"))

   /*
  val master = TestActorRef[MockMaster]
  val masterActor = master.underlyingActor
     */



  "Rooms" should
   {
     //type TestRoom = TestActorRef[RoomActor]
     def underRoom(room:ActorRef):RoomActor =
     {
       room.isInstanceOf[TestActorRef[RoomActor]].shouldEqual(true)
       room.asInstanceOf[TestActorRef[RoomActor]].underlyingActor
     }

     val Some(all) = masterActor.rooms.get("all")
     /*

     var channels = Vector.empty[(Iteratee[JsValue, _], Enumerator[JsValue])]

     def addUser(user:String, password:String)
     {
       val Success(ConnectedSimple(input,output)) = ( master ? SimpleJoin(user,password) ).mapTo[ConnectedSimple].value.get
       channels = channels :+ (input,output)
     }
       */

     addUser(user1,password1)

     addUser(user2,password2)
     addUser(user3,password3)



     val val1:JsValue = Json.obj(
        "channel" -> "messages",
        "content" -> Json.obj(
          "id" -> "someID",
          "user" -> "bob",
          "text" -> "My message is very long"
        ),
        "request"->"save")

     val roomWeb = "SemanticWeb"
     val roomStrategy = "StrategyGroup"

     val mess = Message("someId","someUser","someText")

     "add users to rooms" in {

       masterActor.rooms.size.shouldEqual(1)



       masterActor.add2Room[RoomActor](user1,roomWeb)
       masterActor.rooms.size.shouldEqual(2)

       masterActor.add2Room[RoomActor](user2,roomWeb)
       masterActor.rooms.size.shouldEqual(2)

       masterActor.add2Room[RoomActor](user1, roomWeb)  // to test the duplication
       masterActor.rooms.size.shouldEqual(2)


       val Some(room2) = masterActor.rooms.get(roomWeb)

       val room2U: RoomActor = underRoom(room2)
       room2U.members.size.shouldEqual(2)

       masterActor.add2Room[RoomActor](user3,roomStrategy)
       masterActor.rooms.get(roomStrategy) match
       {
         case None=> this.fail("New room was not added")
         case Some(room)=>
           val u = underRoom(room)
             u.members.size.shouldEqual(1)
       }
       masterActor.rooms.get(roomWeb) match
       {
         case None=> this.fail("New room was not added")
         case Some(room)=>
           val u = underRoom(room)
           u.members.size.shouldEqual(2)
       }
       masterActor.rooms.size.shouldEqual(3)
       masterActor.add2Room[RoomActor](user3,roomWeb)

       masterActor.rooms.get(roomWeb) match
       {
         case None=> this.fail("New room was not added")
         case Some(room)=>
           val u = underRoom(room)
           u.members.size.shouldEqual(3)
       }
       masterActor.rooms.size.shouldEqual(3)

     }


    "let you send info to groups of users" in
    {
      val probes = makeProbes(channels)

       masterActor.rooms.size.shouldEqual(3)

       masterActor.defTestRoom.underlyingActor.members.size.shouldEqual(3)


      val Some(web: ActorRef) = masterActor.rooms.get(roomWeb)
      web ! Tell(mess)

       probes(0).receiveN(1,100 millis)
       probes(1).receiveN(1,100 millis)
       probes(2).receiveN(0,100 millis)


      val Some(strategy) = masterActor.rooms.get(roomStrategy)
      strategy ! Tell(mess)

      probes(0).receiveN(0,100 millis)
      probes(1).receiveN(0,100 millis)
      probes(2).receiveN(1,100 millis)

      masterActor.add2Room[RoomActor](user1,roomStrategy)
      masterActor.add2Room[RoomActor](user2,roomStrategy)
      masterActor.add2Room[RoomActor](user2,roomStrategy) //check if there is no error

      strategy ! Tell(mess)

      probes(0).receiveN(1,100 millis)
      probes(1).receiveN(1,100 millis)
      probes(2).receiveN(1,100 millis)

    }

     "get user status" in
      {
        all  ! GetUsers

        this.expectMsgAllOf[List[String]](200 millis)
        this.expectMsgPF[Unit](200 millis) {
          case RoomMates(list)=>
            list.contains(user1).shouldEqual(true)
            list.contains(user2).shouldEqual(true)
            list.contains(user3).shouldEqual(true)
           (list.size==3).shouldEqual(true)

          case _=>this.fail("We received wrong type of userstatus list")

        }

      }

     "notify on new users" in
     {
       //val res = mutable.Set[JsValue]()
       val probes = this.makeProbes(channels)
       //channels.map{case (input,output)=>output(Iteratee.foreach[JsValue] { event => res + event })}
       //res.size.shouldEqual(0)
       addUser("another1",password1)
       probes(0).receiveN(2)
       probes(1).receiveN(2)

       addUser("another2",password2)
       probes(0).receiveN(2)
       probes(1).receiveN(2)

      }
  }
}