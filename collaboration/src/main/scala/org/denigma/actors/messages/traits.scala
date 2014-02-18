package org.denigma.actors.messages

import java.util.Date
import akka.actor.ActorRef

/**
 * Trait for all classes that contain date
 */
trait DataLike
{
  val date:Date
}

/**
 * trait for all classes that contain value
 * @tparam T type of the value
 */
trait ValueLike[T]
{
  val value:T
}

trait DatedValueLike[T] extends DataLike
{
  val value:T
}




trait EventLike
{
  val name:String
}
