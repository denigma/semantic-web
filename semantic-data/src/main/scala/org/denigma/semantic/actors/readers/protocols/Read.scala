package org.denigma.semantic.actors.readers.protocols

import org.denigma.semantic.commons.QueryLike
import play.api.libs.json._

/*
read only messages
 */
object Read {

  case class Query(query:String,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Select(query:String,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Bind(query:String,binds:Map[String,String],offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Search(query:String,searches:Map[String,String],binds:Map[String,String] = Map.empty,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated

  case class Question(query:String) extends QueryLike

  case class Construct(query:String) extends QueryLike

  /*
  serializers, useful for JSON requests from the client
   */
  object Readers
  {
    implicit val queryReads = Json.reads[Query]

    implicit val selectReads = Json.reads[Select]

    implicit val questionReads = Json.reads[Question]

    implicit val constructReads = Json.reads[Construct]

    implicit val bindReads = Json.reads[Bind]

    implicit val searchReads = Json.reads[Search]


  }



}


trait Paginated extends QueryLike{
  def offset:Long
  def limit:Long
  def isPaginated = offset>0 || limit != Long.MaxValue

}
