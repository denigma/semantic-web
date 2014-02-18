package org.denigma.actors.rooms

import org.denigma.actors.staff.CreativeActor
import scala.reflect.ClassTag
import akka.actor.ActorRef
import akka.testkit.TestActorRef
import org.denigma.actors.rooms.messages.JoinRoom

/**
 * Takes cares of the rooms
 */
trait HouseKeeper extends CreativeActor with MemberHolder{

  var rooms:Map[String,ActorRef] = Map.empty[String,ActorRef]



  def addRoom[T<:RoomActor:ClassTag](roomName:String): ActorRef ={
    val room = this.createActor[T](roomName)
    this.rooms = this.rooms + (roomName->room)
    room
  }

  /**
   * Creates test room
   * @param roomName
   * @tparam T
   * @return
   */
  def addTestRoom[T<:RoomActor:ClassTag](roomName:String): TestActorRef[T] =
  {
    val room: TestActorRef[T] = this.createTestActor[T](roomName)(this.context.system)
    this.rooms = this.rooms + (roomName->room)
    room
  }

  def add2Room[T<:RoomActor:ClassTag](userName:String,roomName:String):Unit =  if(this.allowName(roomName) && this.allowName(userName))
  {
    this.members.get(userName) match
    {
      case Some(actor)=> this.add2Room[T](userName,actor,roomName)
      case None=> this.log.error(s"$name : cannot find user with username = $userName")
    }
  }

  def add2Room[T<:RoomActor:ClassTag](userName:String,actor:ActorRef,roomName:String):Unit=  if(this.allowName(roomName) && this.allowName(userName))
  {
    val roomActor = this.rooms.getOrElse(roomName,this.addRoom(roomName))
    actor ! JoinRoom(roomName,roomActor)
  }
}
