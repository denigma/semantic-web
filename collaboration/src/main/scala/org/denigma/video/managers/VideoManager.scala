package org.denigma.video.managers

import org.denigma.actors.staff.{EventActor, ChatActorLike}
import org.denigma.actors.models.RequestInfo
import org.denigma.actors.rooms.messages.TellUser
import org.denigma.video.rooms.messages.AskVideoRoom

/**
 * Manager that works with video requests
 */
trait VideoManager extends ChatActorLike with EventActor  {

  def receiveVideo:this.Receive= {

    case TellUser(value:RequestInfo,user) => this.sendRequest2Client(value)

    case (channel,"stream", room, content, date) => log.debug("something is streamed")



  }



  def parseVideo:this.ChannelRequestRoomContentDateParser= {


    case (channel,"video", room, content, date) =>
      this.rooms.get(room) match
      {
        case None => parent ! AskVideoRoom(name,room)
        case Some(roomActor) => this.log.error(s"$name videoslot $room was already opened")
      }


    case (channel,request, room, content, date) if request =="ICE" || request =="offer" || request =="answer" =>
      parent ! TellUser(RequestInfo(room,content,request,channel), room)


    case (channel,"stream", room, content, date) => log.debug("something is streamed")

  }

}
