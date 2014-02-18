package org.denigma.video.messages

import org.denigma.actors.messages.ActorEventLike
import org.denigma.actors.staff.ChatActorLike
import akka.actor.ActorRef
import org.denigma.video.managers.BindingManager

/**
 * Changes binding for an actor
 * @param name name of the actor we bind to
 * @param event guard that is used
 * @param actor actor ref to which we bind
 */
case class OpenBinding(name:String,event:BindingManager.ChannelRequestRoomContentDateChecker,actor:ActorRef)// extends ActorEventLike

case class ChangeBinding(name:String,event:String,actor:ActorRef) extends ActorEventLike

case class CloseBinding(name:String,actor:ActorRef) extends ActorEventLike
