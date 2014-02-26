package org.denigma.semantic.actors.writers

import org.denigma.semantic.commons.QueryLike
import org.openrdf.model.URI
import java.io.File
import org.openrdf.rio.RDFFormat
import play.api.libs.json.Json._


/*
Object that contains all Update messages
 */
object Update {

  case class Update(query:String) extends QueryLike

  case class Upload(file:File,contextStr:String)



}
