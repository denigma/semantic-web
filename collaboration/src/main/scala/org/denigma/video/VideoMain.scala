package org.denigma.video

import scala.reflect.ClassTag
import org.denigma.actors.MainActor
import org.denigma.video.rooms._
import org.denigma.video.rooms.messages.AskVideoRoom


//TODO: make the situation more clear with inheritance as it is weird that genes inherit videos
/**
 * Main actor that supports videos
 * @tparam T videomembersubtype
 */
class VideoMain[T<:VideoMember:ClassTag] extends MainActor[T]
{

  override def receive:this.Receive = this.receiveVideo orElse super.receive

  def receiveVideo:this.Receive = {
    case AskVideoRoom(username,room)=>this.add2Room[VideoRoom](username,room)
  }

}
