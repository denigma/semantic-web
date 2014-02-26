package org.denigma.semantic.reading.modifiers

import org.openrdf.model.Value
import org.denigma.semantic.reading.selections._
import scala.util.Try

/*
provides pagination for selects
 */
trait Paginator[T] extends Slicer with ISelect[T]{


  protected def paginatedSelect(str:String,offset:Long,limit:Long):SelectQuerying[T]

  def select(str:String,offset:Long,limit:Long): Try[T] =  this.selectQuery(str,paginatedSelect(str,offset,limit))

}
