package org.denigma.video.rooms

import akka.actor.ActorRef

/**
 * Broadcaster params
 */
class Broadcaster(val actorName:String,val actor:ActorRef) extends  ConnectionStatusManager{

}
