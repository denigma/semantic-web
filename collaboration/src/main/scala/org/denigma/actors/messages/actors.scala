package org.denigma.actors.messages

import akka.actor.{ActorRef, Actor}
import scala.reflect.ClassTag
import org.denigma.actors.staff.NamedActor


/*contains value and sender because message bus does not take senders*/
case class ActorEvent[T](name:String, value:T,actor:ActorRef) extends ActorEventLike

/**
 * Companion object for ActorEvent to do usefull static operations
 */
object ActorEvent {
  def pack(event:EventLike,actor:ActorRef) = ActorEvent(event.name,event,actor)

  def pack[T](name:String,event:T,actor:ActorRef) = ActorEvent[T](name,event,actor)
}
/**
 * Event that has name and actor ref of a sender
 */
trait ActorEventLike extends EventLike
{
  val actor:ActorRef
}

case class Suicide(name:String, value:String, actor:ActorRef)  extends ActorEventLike



/**
 * Used to send robots
 * @param name
 * @param factory
 */
case class Bot[TA<:NamedActor:ClassTag] (name:String,factory:()=>TA)
