package org.denigma.actors.rooms.messages

import org.denigma.actors.messages.{EventLike, ActorEventLike}
import akka.actor.ActorRef

trait RoomEvenLike extends EventLike



case class JoinRoom(room:String,actor:ActorRef) extends RoomEvenLike with ActorEventLike {val name = "join"}
case class UpdateRoom(room:String,actor:ActorRef,connection:String = "all") extends RoomEvenLike with ActorEventLike {val name = "update"}

case class LeaveRoom(room:String,actor:ActorRef) extends RoomEvenLike  with ActorEventLike  {val name = "leave"}
case class AskRoom(room:String) extends RoomEvenLike{val name = "ask_room"}

/*
class some{
  val acc:ActorRef = null
  val n: String = acc.path.name
}
*/
