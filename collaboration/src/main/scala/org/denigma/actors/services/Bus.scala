package org.denigma.actors.services

import akka.event.{LookupClassification, ActorEventBus}
import org.denigma.actors.messages.EventLike

/**
 * Makes event subscriptions
 */
object Bus extends ActorEventBus with LookupClassification
{
  type Event = EventLike
  type Classifier = String

  def mapSize(): Int = 30

  protected def classify(event: Event): Classifier = event.name

  protected def publish(event: Event, subscriber: Subscriber): Unit =  subscriber ! event

}
