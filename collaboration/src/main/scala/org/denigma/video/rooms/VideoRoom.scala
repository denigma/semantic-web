package org.denigma.video.rooms

import akka.actor.ActorRef
import org.denigma.actors.rooms.RoomActor
import org.denigma.actors.models.RequestInfo
import org.denigma.actors.rooms.messages.{LeaveRoom, JoinRoom}
import org.denigma.video.messages.{CloseBinding, OpenBinding}
import org.denigma.video.managers.BindingManager
import java.lang.String
import scala.Predef._
import org.denigma.actors.rooms.messages.JoinRoom
import org.denigma.actors.rooms.messages.LeaveRoom
import org.denigma.actors.models.RequestInfo
import org.denigma.video.rooms.models._
import play.api.libs.json._
import org.denigma.actors.messages.Push
import PeerRequests._


/**
 * Video room to work with videoslots,
 * usually there is one broadcasting and other listening in the slot
 */
class VideoRoom extends RoomActor  with ConnectionStatusManager{
  /**
   * Source actor that streams videosygnal
   */
  var broadcaster:Broadcaster = null

  override def receive: this.Receive = this.receiveUserStatus orElse
    this.receive2Parse orElse
    this.tellCases[JsValue] orElse
    this.tellCases[RequestInfo] orElse
    this.tellDefault

  override def receiveUserStatus = {
    case JoinRoom(username,user)=>
      this.addMember(username,user)
      val binding = this.genRoomBinding(this.name)
      user ! OpenBinding(this.name,binding,self)

    case LeaveRoom(username,user)=>
      this.removeMember(username)
      user ! CloseBinding(this.name,self)
  }


  override def addMember(username:String,user:ActorRef):ActorRef =
  {
    val kv = (username->user)
    members = members+kv
    disconnected = disconnected+kv
    user
  }

  /**
   * removes users from rooms
   * @param username user to be removed
   */
  override def removeMember(username:String)=
  {
    //TODO: fix this indian-style code
    members.get(username) match
    {
      case Some(user)=>
        otherMembers = otherMembers - username
        members = members - username
        connected = connected - username
        pending = pending - username
        disconnected = disconnected - username
      case None=> this.log.error(s"$name : deleting a user $username that is not inside a room")
    }
    //simpleRooms =  simpleRooms.map{case (key,value)=>(key,value-username)}
  }

  def stopBroadcast = {
   this.otherMembers =  Map.empty[String,ActorRef]
   this.pending = Map.empty[String,ActorRef]
   this.disconnected = this.members
   this.broadcaster = null
  }


  def peers2Json(preq:PeerRequest): JsValue =  Json.toJson[PeerRequest](preq)

  def json2Peers(json:JsValue):PeerRequest = Json.fromJson[PeerRequest](json) match
  {
    case JsSuccess(value,path) => value
    case _ =>
      this.log.error(s"$name : video room cannot part peerRequest: ${Json.stringify(json)}")
      null
  }

  def sendConnect(channel:String,room:String)={
    val lst = (for{cb<-this.broadcaster.connected; cc<-this.connected;if cb._1 == cc._1} yield cb._1).toList
    val js = this.peers2Json(CallPeers(this.broadcaster.actorName::Nil,lst))
    if(lst.length>0) this.broadcaster.actor ! Push(now,RequestInfo(channel,js,PeerRequests.CALL_PEERS,room))
  }


  /**
   * Parse what we received
   * @return
   */
  //TODO: rewrite this 'indiad style' code
  def receive2Parse:this.Receive = {
    case RequestInfo(channel,content:JsValue, "broadcast", room) if this.free=>
    this.broadcaster = new Broadcaster(sender.path.name,sender)
        this.otherMembers = this.members - this.broadcaster.actorName
        this.disconnected = this.otherMembers
        this.broadcaster.otherMembers = this.otherMembers
        this.broadcaster.disconnected = this.otherMembers

        val js = this.peers2Json(OpenPeers(this.broadcaster.actorName::Nil,this.otherMembers.keys.toList))
        this.broadcaster.actor ! Push(now,RequestInfo(channel,js,PeerRequests.OPEN_PEERS,room))
        this.otherMembers.foreach
        {
          case (key,value)=>
            val jso = peers2Json(OpenPeers(key::Nil,this.broadcaster.actorName::Nil))
            val reqO = RequestInfo(channel,jso,PeerRequests.OPEN_PEERS,room)
            value ! Push(now,reqO)
            this.moveDisconnectedPending(key,value)
            this.broadcaster.moveDisconnectedPending(key,value) //TODO: rewrite this bad code
        }

    case RequestInfo(channel,content:JsValue, PeerRequests.PEERS_OPENED, room) if this.broadcasting =>
      this.json2Peers(content) match
      {
        case null =>
    //      val doNothing = true
          //do nothing
        case PeersOpened(from:List[String],to:List[String])=>
      //    peersOpened=peersOpened+1
          //TODO: fix this: indian style code
          if(from.head==this.broadcaster.actorName)
            to.foreach(m=> this.broadcaster.movePendingConnected(m))
          else
            from.foreach(m=>this.movePendingConnected(m))
          this.sendConnect(channel,room)

        case _ => this.logWrongParsingTypeFor(PeerRequests.PEERS_OPENED)
      }


    case RequestInfo(channel,content:JsValue, PeerRequests.CLOSE_PEERS, room) if this.broadcasting =>
      this.json2Peers(content) match
      {
        case null => //TODO: write what to do
        case _ => this.logWrongParsingTypeFor(PeerRequests.CLOSE_PEERS)
      }


    case RequestInfo(channel,content:JsValue, PeerRequests.PEERS_CLOSED, room) if this.broadcasting =>
      this.json2Peers(content) match
      {
        case null => //TODO: write what to do
        case _ => this.logWrongParsingTypeFor(PeerRequests.PEERS_CLOSED)
      }




  }
  //var peersOpened = 0

  /**
   * Generates binding for the room
   * @param roomName
   * @return
   */
  def genRoomBinding(roomName:String): BindingManager.ChannelRequestRoomContentDateChecker =
    (channel:String,request:String,room:String, content:JsValue)=> roomName==room


  def free = this.broadcaster==null
  def broadcasting = this.broadcaster!=null
}
