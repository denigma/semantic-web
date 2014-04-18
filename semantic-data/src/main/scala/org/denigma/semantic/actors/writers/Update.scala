package org.denigma.semantic.actors.writers

import org.denigma.semantic.commons.QueryLike
import java.io.File


/*
Object that contains all Update messages
 */
object Update {

  case class Update(query:String) extends QueryLike

  case class Upload(file:File,contextStr:String)



}
