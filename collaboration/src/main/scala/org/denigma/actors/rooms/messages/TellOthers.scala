package org.denigma.actors.rooms.messages

import org.denigma.actors.messages.ValueLike
import org.denigma.actors.models.UserOwned

/**
 * Broadcase message to all users except for specified one
 * @param username
 * @param value
 * @tparam T type of the value
 */
case class TellOthers[T](username:String,value:T) extends ValueLike[T] with UserOwned
