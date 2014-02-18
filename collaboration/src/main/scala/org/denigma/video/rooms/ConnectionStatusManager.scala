package org.denigma.video.rooms

import akka.actor.ActorRef
import akka.testkit.TestActorRef
import scala.Predef._
import scala.Some

trait ConnectionStatusManager{

  var otherMembers = Map.empty[String,ActorRef]
  var connected = Map.empty[String,ActorRef]
  var pending = Map.empty[String,ActorRef]
  var disconnected = Map.empty[String,ActorRef]


  def moveDisconnectedPending(username:String,actor:ActorRef): (String, ActorRef) = {
    this.disconnected = this.disconnected - username
    this.pending = this.pending + (username->actor)
    (username,actor)
  }

  def movePendingConnected(username:String):(String,ActorRef) =
  {
    this.otherMembers.get(username) match
    {
      case Some(actor)=>
        this.movePendingConnected(username,actor)
        (username,actor)
      case None=> ("",null)
    }
  }
  def movePendingConnected(username:String,actor:ActorRef):(String,ActorRef)  = {
    this.pending = this.pending - username
    this.connected = this.connected + (username->actor)
    (username,actor)
  }

  /**
   * Deletes from maps other than disconnected
   * @param username
   * @return
   */
  def disconnect(username:String):(String,ActorRef) =
  {
    this.otherMembers.get(username) match
    {
      case Some(actor)=>
        this.disconnect(username,actor)
        (username,actor)
      case None=> ("",null)
    }
  }

  def disconnect(username:String,actor:ActorRef):(String,ActorRef)  = {
    this.disconnected + username->actor
    this.pending = this.pending - username
    this.connected = this.connected - username
    (username,actor)
  }
  //var broadcasting = Map.empty[String,ActorRef]


}
