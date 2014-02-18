package org.denigma.actors.rooms

import org.denigma.actors.models._
import play.api.libs.json.JsValue
import org.denigma.actors.rooms.messages.{LeaveRoom, RoomMates, JoinRoom}
import org.denigma.actors.rooms.messages.JoinRoom
import org.denigma.actors.models.Message
import org.denigma.actors.rooms.messages.LeaveRoom
import org.denigma.actors.rooms.messages.RoomMates
import org.denigma.actors.models.Task
import org.denigma.actors.models.RequestInfo
import java.util.Date
import scala.collection.immutable.SortedSet
import org.denigma.actors.messages.Received

/**
 * For chat communications
 */
class ChatRoomActor extends RoomActor
{

  override def receive = this.receiveUserStatus orElse
    this.tellCases[Message] orElse
    this.tellCases[Task] orElse
    this.tellCases[JsValue] orElse
    this.tellCases[RequestInfo] orElse
    this.tellDefault


  override def receiveUserStatus:this.Receive = {

    case JoinRoom(username,user)=>
      this.addMember(username,user)
      this.tellOthers(username,UserJoined(username))
      user ! RoomMates(this.members.keys.toList)

    case LeaveRoom(username,user)=>
      this.removeMember(username)
      this.tell(UserLeft(username))

    case GetUsers => sender ! RoomMates(this.members.keys.toList)

    case st:UserStatusLike=>this.tellOthers(st.username,st)


  }
}
