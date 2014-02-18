package org.denigma.actors.staff

import akka.event._
import akka.util.Subclassification
import akka.actor.ActorRef
import akka.event
import org.denigma.actors.messages.{EventLike, ActorEvent}
import org.denigma.actors.services.Bus

/**
 * Actors that work with eventbuss events
 */
trait EventActor extends NamedActor with LogActor{

  implicit def futureContext = this.context.dispatcher

  /**
   * Subscribe actor to the event
   * @param subscriber actor to be subscribed
   * @param channel class which messages should go to the actor
   * @return if subscribtion is ok
   */
  def subscribe(subscriber: ActorRef, channel:String): Boolean = Bus.subscribe(subscriber,channel)

  /**
   * Subscribes actor to the event
   * @param subscriber
   * @param channel
   * @return if subscribtion is ok
   */
  def unsubscribe(subscriber: ActorRef, channel:String): Boolean = Bus.unsubscribe(subscriber,channel)

  /**
   * Unsubscribe actor from the event
   * @param subscriber
   */
  def unsubscribe(subscriber: ActorRef): Unit = Bus.unsubscribe(subscriber)

  /**
   * Publishes any event to eventbus
   * @param event
   */
  def publish(event:EventLike) = Bus.publish(event)

  def on[T](name:String,value:T) = Bus.subscribe(self,name)
  def fire[T](actorEvent:ActorEvent[T]) = Bus.publish(actorEvent)

}






