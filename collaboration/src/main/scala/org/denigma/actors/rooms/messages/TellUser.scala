package org.denigma.actors.rooms.messages

/**
 * Tells something to specified user
 * @param value
 * @param user
 * @tparam T
 */
case class TellUser[T](value:T, user:String)
