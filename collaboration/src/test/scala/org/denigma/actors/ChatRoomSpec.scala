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
import org.denigma.actors.rooms.{ChatRoomActor, RoomActor}
import org.denigma.actors.base.MainSpec
import org.denigma.actors.base.BasicSpec
import org.denigma.actors.base.{MainSpec}
import org.denigma.actors.mock.MockMaster
import org.denigma.actors.models.Message
import org.denigma.actors.rooms.messages.Tell
import org.denigma.actors.rooms.messages.RoomMates

class ChatRoomSpec(_system: ActorSystem)  extends MainSpec[MockMaster](_system)//extends BasicSpec(_system)
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

      "be added in mocks as testrooms" in {
        masterActor.addTestRoom[ChatRoomActor](roomWeb)
        masterActor.addTestRoom[ChatRoomActor](roomStrategy)
        masterActor.rooms.get(roomWeb).get.isInstanceOf[TestActorRef[ChatRoomActor]].shouldEqual(true)
        masterActor.rooms.get(roomStrategy).get.isInstanceOf[TestActorRef[ChatRoomActor]].shouldEqual(true)
      }

//      val web = masterActor.rooms.get(roomWeb).get.asInstanceOf[TestActorRef[ChatRoomActor]]
//      val webActor = web.underlyingActor
//
//      "work with databases" in {
//
//      }



    }
}