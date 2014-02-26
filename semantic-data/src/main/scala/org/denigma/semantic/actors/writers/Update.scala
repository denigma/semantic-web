package org.denigma.semantic.actors.writers

import org.denigma.semantic.commons.QueryLike


/*
Object that contains all Update messages
 */
object Update {

  case class Update(query:String) extends QueryLike

}
