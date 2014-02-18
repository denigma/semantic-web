package org.denigma.video

import org.denigma.actors.Member
import org.denigma.video.managers.{VideoManager, BindingManager}

/**
 * Actor that can handle video requests
 */
class VideoMember extends Member with BindingManager with VideoManager{



  override def preStart() = {
    log.debug(s"Starting VideoMember actor with name = ${self.path.name}")
    this.checkConnectionTimeout()
  }


  /**
   * Actor Receive function that process all actor's messages
   * @return
   */
  override def receive = this.receiveBindings orElse this.receiveVideo orElse super.receive

  override def parseOperations: this.ChannelRequestRoomContentDateParser= this.parseBinding orElse  this.parseVideo orElse  super.parseOperations

}
