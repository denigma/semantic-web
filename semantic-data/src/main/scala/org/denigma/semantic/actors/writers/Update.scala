package org.denigma.semantic.actors.writers

import org.denigma.semantic.commons.QueryLike
import java.io.File

import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.{PropertyModel, Shape}


/*
Object that contains all Update messages
 */
object Update {

  case class Update(query:String) extends QueryLike

  case class Upload(file:File,contextStr:String)

  case class UpdateShape(shape:Shape,contexts:List[Res] = List.empty)

  case class AddShape(shape:Shape,contexts:List[Res] = List.empty)

  case class AddModels(models:Set[PropertyModel],shape:Option[Shape] = None,contexts:List[Res] = List.empty)





}
