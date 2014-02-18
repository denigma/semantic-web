package org.denigma.video

import  scala.language.postfixOps


import scalax.collection.mutable.{Graph => MGraph}


import akka.testkit._

import akka.actor._
import play.api.libs.json._
import  scala.language.postfixOps


import scalax.collection.mutable.{Graph => MGraph}
import org.denigma.video.mock.{MockVideoRoom, MockVideoMaster}
import org.denigma.video.rooms.models._
import play.api.libs.json.JsSuccess
import PeerRequests._
import org.denigma.actors.base.MainSpec


/**
 * TestSpec to test videoroom
 * @param _system Test actor system, will be based automaticaly by test akk package
 */
class VideoRoomSpec(_system: ActorSystem) extends MainSpec[MockVideoMaster](_system)//  extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{


  def this() = this(ActorSystem("MySpec"))

  "VideoRoom" should {


    addUser(user1,password1)
    addUser(user2,password2)
    addUser(user3,password3)

    def roomUnder(roomActor:ActorRef):MockVideoRoom = {
      roomActor.isInstanceOf[TestActorRef[MockVideoRoom]].shouldEqual(true)
      roomActor.asInstanceOf[TestActorRef[MockVideoRoom]].underlyingActor
    }

    "Parse connection requests well" in {
      val fromArr =  Json.arr("userFrom1","userFrom2","userFrom3")
      val toArr = Json.arr("userTo1","userTo2")

      val callPeers:JsValue = Json.obj(
      "from"->fromArr,
      "to"->toArr,
      "name"->PeerRequests.CALL_PEERS
      )

      val jCallPeersStream: JsResult[PeerRequest] = Json.fromJson[PeerRequest](callPeers)
      val callPeersStream = jCallPeersStream.asInstanceOf[JsSuccess[PeerRequest]].value
      callPeersStream match {
        case cp @  CallPeers(from,to)=>
          cp.name.shouldEqual(PeerRequests.CALL_PEERS)
          from.length.shouldEqual(3)
          to.length.shouldEqual(2)
          from.head.shouldEqual("userFrom1")
          to.head.shouldEqual("userTo1")
          to(1).shouldEqual("userTo2")

        case _ =>this.fail(s"wrong  ${PeerRequests.CALL_PEERS} parsing")
      }
      val openPeers = Json.obj(
        "from"->fromArr,
        "to"->toArr,
        "name"->PeerRequests.OPEN_PEERS
      )

      val jOpenPeersStream = Json.fromJson[PeerRequest](openPeers)
      val openPeersStream = jOpenPeersStream.asInstanceOf[JsSuccess[PeerRequest]].value
      openPeersStream match {
        case cp @  OpenPeers(from,to)=>
          cp.name.shouldEqual(PeerRequests.OPEN_PEERS)
          from.length.shouldEqual(3)
          to.length.shouldEqual(2)
          from.head.shouldEqual("userFrom1")
          to.head.shouldEqual("userTo1")
          to(1).shouldEqual("userTo2")

        case _ =>this.fail(s"wrong ${PeerRequests.OPEN_PEERS} parsing")
      }

      val free = Json.obj(
        "from"->fromArr,
        "to"->toArr,
        "name"->"freeRequest"
      )

      val jFreeStream = Json.fromJson[PeerRequest](free)
      val freeStream = jFreeStream.asInstanceOf[JsSuccess[PeerRequest]].value

      freeStream match {
        case cp @  FreePeers(name,from,to)=>
          name.shouldEqual("freeRequest")
          from.length.shouldEqual(3)
          to.length.shouldEqual(2)
          from.head.shouldEqual("userFrom1")
          to.head.shouldEqual("userTo1")
          to(1).shouldEqual("userTo2")

        case st =>
          this.fail(s"wrong ${FreePeers} parsing")
      }

    }
    /*
    "Work well with statuses" in {

    }
    */


  }
}
