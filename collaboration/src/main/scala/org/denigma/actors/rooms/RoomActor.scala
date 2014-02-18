package org.denigma.actors.rooms


import org.denigma.actors.models._
import play.api.libs.json.JsValue

import org.denigma.actors.models.RequestInfo
import org.denigma.actors.models.Message
import org.denigma.actors.models.Task
import scala.Some
import org.denigma.actors.models.UserLeft
import org.denigma.actors.models.UserJoined
import akka.actor.ActorRef
import org.denigma.actors.rooms.messages._
import org.denigma.actors.messages.Push
import org.denigma.actors.staff.EventActor
import org.denigma.actors.rooms.messages.LeaveRoom
import org.denigma.actors.models.RequestInfo
import org.denigma.actors.rooms.messages.JoinRoom
import org.denigma.actors.models.Message
import org.denigma.actors.rooms.messages.Tell
import org.denigma.actors.models.Task
import scala.Some
import org.denigma.actors.rooms.messages.TellOthers
import org.denigma.actors.messages.Push
import org.denigma.actors.models.UserLeft
import org.denigma.actors.models.UserJoined

/**
 * This actors if for rooms
 */
class RoomActor extends EventActor with MemberHolder{


  /** *
    * Input of room actor
    * Room actor works only with websocket request and opens/closes socket connections when needed
    * @return
    */
  def receive = this.receiveUserStatus orElse
    this.tellCases[JsValue] orElse
    this.tellCases[RequestInfo] orElse
    this.tellDefault

  def receiveUserStatus:this.Receive = {

    case JoinRoom(username,user)=>
      this.addMember(username,user)


    case LeaveRoom(username,user)=>
      this.removeMember(username)
  }


  def addMember(username:String,user:ActorRef):ActorRef =
  {
    members = members+(username->user)
    user
  }

  /**
   * removes users from rooms
   * @param username user to be removed
   */
  def removeMember(username:String)=
  {
    members.get(username) match
    {
      case Some(user)=>

        members = members - username
      case None=> this.log.error("$name : deleting a user $username that is not inside a room")
    }
    //simpleRooms =  simpleRooms.map{case (key,value)=>(key,value-username)}
  }


  /**
   * Generates tell partial function for specified type of input
   * @tparam T type of message
   * @return receive partical function
   */
  def tellCases[T]: this.Receive = this.tellUser[T] orElse
    {
      case Tell(value: T)  => tell[T](value)
      case TellOthers(username:String,value:T) =>  tellOthers(username,value)
    }

  def tellDefault:PartialFunction[Any,Unit] =
  {
    case Tell(value)  => log.info(s"$name : unknown Tell type ${value.toString}")
    case TellOthers(username:String,value)  => log.info(s"$name : from $username received unknown TellOthers type ${value.toString}")

  }

  /**
   * broadcast info to other users that are inside the specified room
   * @param username the user that does broadcast, everybody in the room except for him will receive the data
   * @param value value to be sent
   * @tparam T
   */
  def tellOthers[T](username:String,value:T): Unit = {

    this.members.foreach{case (key,actor)=> if(key!=username) actor ! Push(now,value) }
    log.debug(s"$name: $username tellOthers ${value.toString}")
  }

  /*
  protected def broadcast[T](value:T, actors:Map[String,ActorRef], room:Set[String]):Unit =
    actors.foreach{case (key,actor:ActorRef)=>if(room.contains(key)) actor ! Push(now,value)}
    */
  /**
   * Broadcasts info to all participants
   * @param value what to broadcast
   * @tparam T
   */
  def tell[T](value:T): Unit = {
    this.members.values.foreach(actor=>actor ! Push(now,value))
    log.debug(s"${name} tellAll ${value.toString}")
  }

}
