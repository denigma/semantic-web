package org.denigma.semantic.actors.writers

import org.denigma.semantic.commons.QueryLike
import java.io.File

import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.Shape


/*
Object that contains all Update messages
 */
object Update {

  case class Update(query:String) extends QueryLike

  case class Upload(file:File,contextStr:String)

  case class UpdateShape(shape:Shape,context:Option[Res])

  case class AddShape(shape:Shape,context:Option[Res])





}
