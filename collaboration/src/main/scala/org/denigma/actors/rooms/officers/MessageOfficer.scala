package org.denigma.actors.rooms.officers

import play.api.libs.json.{JsError, JsSuccess, JsValue}
import java.util.Date
import org.denigma.actors.messages.Received
import org.denigma.actors.models.Message
import org.denigma.actors.staff.{EventActor, ChatActorLike}
import scala.collection.immutable.SortedSet
import org.denigma.actors.orderings.DataOrdering

/**
 * Room trate that works with messages
 */
trait MessageOfficer  extends ChatActorLike
{
  /*
  /**
   * here we stored messages that we received
   */
  protected var _messages = SortedSet.empty[Received[Message]](new DataOrdering[Received[Message]])

  implicit def messages = _messages

  implicit def messages_= (value:SortedSet[Received[Message]]): Unit = _messages = value

  def addMessage(mess:Message)(implicit date:Date) =
  {
    this.addItem(mess)

  }
  */

}
