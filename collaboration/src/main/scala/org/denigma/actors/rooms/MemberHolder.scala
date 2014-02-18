package org.denigma.actors.rooms

import org.denigma.actors.rooms.messages.TellUser
import akka.actor.{Actor, ActorRef}
import org.denigma.actors.staff.{ChatActorLike, EventActor}
import scala.util.matching.Regex

/**
 * Trait for all actors that have users inside
 */
trait MemberHolder extends EventActor
{
  var members = Map.empty[String,ActorRef]
  val ActorRegex = """(?:[-\w:@&=+,.!~*'_;]|%\p{XDigit}{2})(?:[-\w:@&=+,.!~*'$_;]|%\p{XDigit}{2})*""".r

  def tellUser[T]: this.Receive  = {
    case tu @ TellUser(value:T,user:String) => this.members.get(user) match
    {
      case Some(ref: ActorRef) => ref forward tu
      case None => log.error(s"${name}: TellUser message is sent to $user user that does not exist")
    }

  }

  /**
   * Checks if name is permitted
   * @param actorName
   * @return
   */
  def allowName(actorName: String): Boolean = actorName match
  {
    case null           ⇒
      this.log.error(s"actor name cannot be null")
      false
    case ""             ⇒
      this.log.error(s"actor name cannot be empty")
      false
    case ActorRegex() ⇒ true
    case _              ⇒
      this.log.error(s"actor name '$actorName' is wrong")
      false
  }


}
