package org.denigma.semantic.reading.modifiers

import scala.util.Try
import org.denigma.semantic.reading.ISelect

/*
provides pagination for selects
 */
trait Paginator[T] extends Slicer with ISelect[T]{


  protected def paginatedSelect(str:String,offset:Long,limit:Long,rewrite:Boolean):SelectHandler[T]

  def select(str:String,offset:Long,limit:Long,rewrite:Boolean): Try[T] =  this.selectQuery(str)(paginatedSelect(str,offset,limit,rewrite))

}
