package org.denigma.video.messages

import akka.actor.ActorRef
import org.denigma.actors.messages.ActorEventLike

/**
 * Used to send actor refs
 * @param name
 * @param actor
 */
case class ActorInside(name:String,actor:ActorRef)  extends ActorEventLike
