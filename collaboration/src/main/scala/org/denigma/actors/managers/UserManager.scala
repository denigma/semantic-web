package org.denigma.actors.managers

import scala.concurrent.duration._
import scala.collection.immutable.SortedSet
import org.denigma.actors.messages.{Suicide, Push, Received}
import org.denigma.actors.models._
import org.denigma.actors.orderings.DataOrdering
import play.api.libs.json.{Json, JsError, JsSuccess, JsValue}
import java.util.Date
import org.denigma.actors.staff.ChatActorLike
import akka.actor.ActorRef
import org.denigma.actors.rooms.messages.{RoomMates, LeaveRoom, JoinRoom}
import akka.pattern.ask

/**
 * Usermanager trait that is used to show list of users and tell about user events
 */
trait UserManager  extends ChatActorLike with UserStatusFormatter with MessageManager
{

  val killTimeout = 10 seconds
  def scheduler = this.context.system.scheduler


  def receiveRoom:this.Receive = {

    case JoinRoom(roomName,roomActor) =>
      this.rooms = this.rooms + (roomName ->roomActor)
      roomActor ! JoinRoom(name,self)

    case RoomMates(list)=>
          list.foreach{
            u=> val uc = UserConnected(u)
            this.sendJson2Client(this.packUser(uc))(this.now)}


    case LeaveRoom(roomName,roomActor) =>
      this.rooms = this.rooms - roomName
      roomActor ! LeaveRoom(name,self)//UserLeft(name)

  }

  def packUser(userStatus:UserStatusLike): List[JsValue]= userStatus match
  {

    case UserJoined(user)=>
      val mess = Message(this.makeId,userStatus.username,s"${userStatus.username} ${userStatus.status}")
      pack2Request(mess,"push")::pack2Request(userStatus,"push")::Nil

    case UserLeft(user)=>
      val mess = Message(this.makeId,userStatus.username,s"${userStatus.username} ${userStatus.status}")
      pack2Request(mess,"push")::pack2Request(userStatus,"delete")::Nil

    case other:UserStatusLike=>this.pack2Request(userStatus,"push")::Nil

  }

  def pack2Request(status:UserStatusLike,request:String):JsValue = Json.toJson(pack2RequestInfo(status,request))(writeRequestInfo)

  def pack2RequestInfo(userStatus:UserStatusLike,request:String): RequestInfo = {
    val content: JsValue =  Json.toJson(userStatus)(writeUserStatus)
    RequestInfo("users",content,request)
  }

  def receiveStatus:  this.Receive = {

    case Push(date, value:UserStatusLike) => sendJson2Client(packUser(value))(date)

  }

  def parseUsers:this.ChannelRequestRoomContentDateParser = {

    case ("users","all", everybody, content, date) => //donothing

    case ("users",req, room, content, date) =>  this.processUsers(req,content)(date)

  }

  def processUsers(req:String,content:JsValue)(implicit date:Date)= req match {

    case _ => //Nothing

  }

  /**
   * Kills actor if it has not received a connection for a long time
   * @return
   */
  def checkConnectionTimeout() = {
    this.scheduler.scheduleOnce(this.killTimeout)(this.toBeOrNotToBe)(this.futureContext)

  }
  def toBeOrNotToBe() = if(this.channels.isEmpty) this.lonelyApoptosis()

  def lonelyApoptosis() = {
    val reason = s"actor $name has not received a connection channel for a long period of time and decides to die of loneliness"
    parent ! Suicide(name,reason,self)
  }

  /**
   * kils actors when something went
   */
  def apoptosis() = {
    parent

  }



  /**
   * Events to be executed when actor started
   */
  override def preStart() = {
    log.debug(s"$name STARTED")
    this.checkConnectionTimeout()
  }


  override def postStop()
  {
    this.rooms.values.foreach{r:ActorRef=>
      //r ! UserLeft(this.name)
      r ! LeaveRoom(name,self)

    }
    this.rooms = Map.empty[String,ActorRef]
  }


}
