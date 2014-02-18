package org.denigma.actors.rooms.officers

import org.denigma.actors.staff.EventActor
import org.denigma.actors.models.IdOwner
import java.util.Date
import scala.collection.immutable.SortedSet
import org.denigma.actors.messages.Received

/**
 * Room officer class
 */
trait RoomOfficer extends EventActor{
 /*
  /**
   * Add item to the map inside the actor
   * @param model model to be saved
   * @param date date when it happened
   * @param getter getter that gets the item from the Map
   * @param setter setter that add the item
   * @tparam T type of the item
   * @return
   */
  protected def addItem[T<:  IdOwner](model:T)(date:Date)(implicit getter:SortedSet[Received[T]], setter: SortedSet[Received[T]] => Unit ):Option[Received[T]] =   {
    val data = Received(date,model)
    setter(this.getWithout(getter,data.value.id) + data)
    return Some(data)
  }

  def add()

*/
}
