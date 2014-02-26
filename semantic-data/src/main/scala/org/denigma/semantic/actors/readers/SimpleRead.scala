package org.denigma.semantic.actors.readers

import org.openrdf.model.Value
import org.denigma.semantic.commons.QueryLike

/*
TODO: REWRITE IN A BETTER WAY
*/
//object SimpleRead {
//
//  trait Simple
//
//  case class Select(query:String,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated with Simple
//
//  case class BindedSelect(query:String,params:(String,Value),offset:Long = 0, limit:Long = Long.MaxValue) extends Simple
//
//  case class Question(query:String) extends QueryLike with Simple
//
//  case class Construct(query:String) extends QueryLike with Simple
//
//  case class Bind(query:String,params:Map[String,Value]) extends QueryLike  with Simple
//
//  case class BindPaginated(query:String,offset:Long = 0, limit:Long = Long.MaxValue,params:Map[String,Value]) extends Paginated  with Simple
//
//}