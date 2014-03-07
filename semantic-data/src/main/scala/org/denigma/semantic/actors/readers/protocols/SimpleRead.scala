package org.denigma.semantic.actors.readers.protocols

import org.denigma.semantic.commons.QueryLike
import play.api.libs.json.Json

/*
TODO: REWRITE IN A BETTER WAY
*/
object SimpleRead {

  trait SimpleQuery extends QueryLike


  case class Select(query:String,offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated with SimpleQuery

  case class Bind(query:String,binds:Map[String,String],offset:Long = 0, limit:Long = Long.MaxValue) extends Paginated  with SimpleQuery

  case class Search(query:String,searches:Map[String,String],binds:Map[String,String] = Map.empty,offset:Long = 0, limit:Long = Long.MaxValue,params:Map[String,String]) extends Paginated with SimpleQuery

  case class Question(query:String) extends QueryLike with SimpleQuery

  case class Construct(query:String) extends QueryLike with SimpleQuery

  /*
   serializers, useful for JSON requests from the client
    */
  object Readers
  {

    implicit val simpleSelectReads = Json.reads[Select]

    implicit val simpleQuestionReads = Json.reads[Question]

    implicit val simpleConstructReads = Json.reads[Construct]

    implicit val simpleBindReads = Json.reads[Bind]

    implicit val simpleSearchReads = Json.reads[Search]


  }
}