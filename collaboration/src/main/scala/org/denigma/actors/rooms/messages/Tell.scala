package org.denigma.actors.rooms.messages

import org.denigma.actors.messages.ValueLike

/**
 * Broadcast message to all users
 * @param value
 * @tparam T type of the value
 */
case class Tell[T](value:T) extends ValueLike[T]
