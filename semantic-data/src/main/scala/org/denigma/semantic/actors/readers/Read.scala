package org.denigma.semantic.actors.readers

import org.openrdf.model.Value
import org.denigma.semantic.commons.QueryLike

/*
read only messages
 */
object Read {

  case class Query(query:String,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Select(query:String,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class BindedSelect(query:String,params:(String,Value),offset:Long = 0, limit:Long = Long.MaxValue) extends QueryLike

  case class Question(query:String) extends QueryLike

  case class Construct(query:String) extends QueryLike

  case class Bind(query:String,params:Map[String,Value]) extends QueryLike

  case class BindPaginated(query:String,offset:Long = 0, limit:Long = Long.MaxValue,params:Map[String,Value]) extends Paginated




}


trait Paginated extends QueryLike{
  def offset:Long
  def limit:Long
  def isPaginated = offset>0 || limit != Long.MaxValue

}
