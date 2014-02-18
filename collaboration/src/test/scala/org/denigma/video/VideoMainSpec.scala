package org.denigma.video
import org.denigma.video.mock._

import  scala.language.postfixOps


import scalax.collection.mutable.{Graph => MGraph}

import scala.concurrent.duration._


import akka.testkit._

import akka.actor._
import play.api.libs.json._
import  scala.language.postfixOps


import scalax.collection.mutable.{Graph => MGraph}
import org.denigma.video.messages.ChangeBinding
import org.denigma.video.rooms.models.PeerRequests._
import org.denigma.actors.base.MainSpec
import org.denigma.actors.messages.Received

/**
 * Testspec for
 * @param _system
 */
class VideoMainSpec(_system: ActorSystem) extends MainSpec[MockVideoMaster](_system)// extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{



  def this() = this(ActorSystem("MySpec"))

  "VideoMain" should {


       addUser(user1,password1)
       addUser(user2,password2)
       addUser(user3,password3)

      def roomUnder(roomActor:ActorRef):MockVideoRoom = {
        roomActor.isInstanceOf[TestActorRef[MockVideoRoom]].shouldEqual(true)
        roomActor.asInstanceOf[TestActorRef[MockVideoRoom]].underlyingActor
      }



    val(Some(u1),Some(u2),Some(u3)) =
        (masterActor.testMembers.get(user1),masterActor.testMembers.get(user2),masterActor.testMembers.get(user3))
      val (ua1,ua2,ua3) = (u1.underlyingActor,u2.underlyingActor,u3.underlyingActor)


      val probes = this.makeProbes(channels)


      val cont1 = Json.obj(
        "name" -> "open")

      val openSlot1:JsValue = Json.obj(
        "channel"->"slot1",
        "content" -> cont1,
        "request"->"video",
        "room"->"slot1"
      )

    val openSlot2:JsValue = Json.obj(
      "channel"->"slot2",
      "content" -> cont1,
      "request"->"video",
      "room"->"slot2"
    )


    val probe1 = new TestProbe(_system)
    val probe2 = new TestProbe(_system)

    "Add videorooms" in
     {
       ua1.rooms.size.shouldEqual(1)
       masterActor.rooms.size.shouldEqual(1)
       u1 ! Received(ua1.now,openSlot1)
       //ua1.parent ! AskVideoRoom(ua1.name,"slot1")
       masterActor.rooms.size.shouldEqual(2)
       ua1.rooms.size.shouldEqual(2)

       val Some(slr1) = masterActor.rooms.get("slot1")
       slr1 ! probe1

       val slot1 = roomUnder(slr1)

       slot1.members.size.shouldEqual(1)

       ua1.rooms.size.shouldEqual(2)
       ua2.rooms.size.shouldEqual(1)
       ua3.rooms.size.shouldEqual(1)


       u2 ! Received(ua2.now,openSlot1)
       probe1.expectMsgPF[ChangeBinding](200 millis)({
         case ch @ ChangeBinding(name:String,"opened",actor)=> ch
         case _ =>this.fail("strange stuff receved")
       })
       masterActor.rooms.size.shouldEqual(2)

       slot1.members.size.shouldEqual(2)
       ua1.rooms.size.shouldEqual(2)
       ua2.rooms.size.shouldEqual(2)
       ua3.rooms.size.shouldEqual(1)

       u2 ! Received(ua2.now,openSlot2)
       masterActor.rooms.size.shouldEqual(3)

       slot1.members.size.shouldEqual(2)

       val Some(slr2:ActorRef) = masterActor.rooms.get("slot2")
       slr2 ! probe2

       val slot2 = roomUnder(slr2)
       slot2.members.size.shouldEqual(1)

       ua1.rooms.size.shouldEqual(2)
       ua2.rooms.size.shouldEqual(3)
       ua3.rooms.size.shouldEqual(1)


       u3 ! Received(ua2.now,openSlot2)

       probe2.expectMsgPF[ChangeBinding](200 millis)({
         case ch @ ChangeBinding(name:String,"opened",actor)=> ch
         case _ =>this.fail("strange stuff receved")
       })

       masterActor.rooms.size.shouldEqual(3)
       slot1.members.size.shouldEqual(2)
       slot2.members.size.shouldEqual(2)


       ua1.rooms.size.shouldEqual(2)
       ua2.rooms.size.shouldEqual(3)
       ua3.rooms.size.shouldEqual(2)

     }


    val weird1:JsValue = Json.obj(
      "channel"->"somechannel",
      "content" -> cont1,
      "request"->"slot1: something_weird_to_test_room_binding",
      "room"->"slot1"
    )

    val weird2:JsValue = Json.obj(
      "channel"->"somechannel",
      "content" -> cont1,
      "request"->"slot2: something_weird_to_test_room_binding",
      "room"->"slot2"
    )

    "bind well" in
    {
      masterActor.rooms.size.shouldEqual(3)

     val Some(slr1:ActorRef) = masterActor.rooms.get("slot1")

      val Some(slr2:ActorRef) = masterActor.rooms.get("slot2")

      slr1 ! probe1
      slr2 ! probe2

      u1 ! Received(ua1.now,weird1)
      u1 ! Received(ua1.now,weird1)
      probe1.receiveN(2, 200 millis)

      u1 ! Received(ua1.now,weird2)
      probe1.expectNoMsg(200 millis)
      probe2.expectNoMsg(200 millis)

      u2 ! Received(ua1.now,weird2)
      probe1.expectNoMsg(200 millis)
      //probe1.receiveN(1, 200 millis)
      probe2.receiveN(1, 200 millis)
    }


    val second:JsValue = Json.obj(
      "channel"->"slot2",
      "content" -> cont1,
      "request"->"broadcast",
      "room"->"slot2"
    )


    val opened32 = PeersOpened(user3::Nil,user2::Nil)
    val opened23 = PeersOpened(user2::Nil,user3::Nil)

    val peersOpened32 = Json.toJson[PeerRequest](opened32)
    val peersOpened23 = Json.toJson[PeerRequest](opened23)


    val secondOpened32:JsValue = Json.obj(
      "channel"->"slot2",
      "content" -> peersOpened32,
      "request"->PEERS_OPENED,
      "room"->"slot2"
    )

    val secondOpened23:JsValue = Json.obj(
      "channel"->"slot2",
      "content" -> peersOpened23,
      "request"->PEERS_OPENED,
      "room"->"slot2"
    )

    "do broadcasting" in
      {
      val mes = Received(ua2.now,second)
      val Some(slr2) = masterActor.rooms.get("slot2")

        //val p = slr2
      val slot2 = roomUnder(slr2)

        slot2.members.size.shouldEqual(2)
      slot2.free.shouldEqual(true)
      slot2.disconnected.size.shouldEqual(slot2.members.size)
      slot2.pending.shouldEqual(Map.empty[String,ActorRef])


      //val req = RequestInfo("slot2",cont1,"broadcast","slot2")
      u2 ! Received(ua2.now,second)
      probes(0).expectNoMsg(200 millis)
      probes(1).receiveN(1,200 millis)
      probes(2).receiveN(1,200 millis)

      slot2.broadcaster.actor.shouldEqual(u2)

        slot2.disconnected.size.shouldEqual(0)
        slot2.pending.size.shouldEqual(1)
        slot2.connected.size.shouldEqual(0)
        val br = slot2.broadcaster

        br.disconnected.size.shouldEqual(0)
        br.pending.size.shouldEqual(1)
        br.connected.size.shouldEqual(0)



        u3 ! Received(ua3.now,secondOpened32)


        slot2.disconnected.size.shouldEqual(0)
        slot2.pending.size.shouldEqual(0)
        slot2.connected.size.shouldEqual(1)

        br.disconnected.size.shouldEqual(0)
        br.pending.size.shouldEqual(1)
        br.connected.size.shouldEqual(0)



        u2 ! Received(ua3.now,secondOpened23)

        slot2.disconnected.size.shouldEqual(0)
        slot2.pending.size.shouldEqual(0)
        slot2.connected.size.shouldEqual(1)

        br.disconnected.size.shouldEqual(0)
        br.pending.size.shouldEqual(0)
        br.connected.size.shouldEqual(1)



        //probes(1).receiveN(1)
        probes(1).expectMsgPF[JsValue](200 millis){
          case js:JsValue=>
            val cont: JsValue = (js \ "content")
            (cont \ "name").asOpt[String].shouldEqual(Some(CALL_PEERS))
            js

          case _=>this.fail("wrong js received")

        }
        probes(2).expectNoMsg(200 millis)


      }
  }

}
