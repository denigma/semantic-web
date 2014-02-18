package org.denigma.video.mock

import scala.reflect.ClassTag
import akka.testkit.TestActorRef
import akka.actor.ActorRef
import org.denigma.video.{VideoMain, VideoMember}
import org.denigma.actors.rooms.{ChatRoomActor, RoomActor}
import org.denigma.video.rooms.messages.AskVideoRoom
import org.denigma.video.rooms.VideoRoom

class MockVideoMaster extends MockVideoMain[VideoMember]



class MockVideoMain[T<:VideoMember:ClassTag]  extends VideoMain[T] {
  var testMembers = Map.empty[String,TestActorRef[T]]
  var defTestRoom: TestActorRef[ChatRoomActor] = null


  override def preStart() = {
    this.initAuth()
    this.defTestRoom = this.addTestRoom[ChatRoomActor]("all")
    this.defaultRoom = this.rooms.head
    log.debug(s"$name started")
  }


  /* creates TestActorRefs instead of real once
   * @param username
   * @return
   */
  override def addMember(username:String):ActorRef =
  {
    val user = this.createTestActor[T](username)(context.system)
    members+=username->user
    testMembers+=username->user
    this.onAddMember(user)
    user
  }

  override def addRoom[T<:RoomActor:ClassTag](roomName:String): ActorRef ={
    this.addTestRoom[T](roomName)
  }

  override def receiveVideo:this.Receive = {
    case AskVideoRoom(username,room)=>this.add2Room[MockVideoRoom](username,room)
  }



}
