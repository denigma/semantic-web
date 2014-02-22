package org.denigma.semantic.actors

import akka.actor.Actor
import org.denigma.semantic.data._
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.collection.immutable.{Set, Vector, Map, List}
import play.api.libs.json.{Json, JsValue, JsObject}
import scala.Predef.Set
import scala.reflect.runtime.universe._
import org.denigma.semantic.WI

/*
actors that contains db and can process something
 */
abstract class DBActor(val db:RDFStore) extends  NamedActor{

}

object Data{
//
//  type Reading[T] = BigdataSailRepositoryConnection=>T
//  type Writing = BigdataSailRepositoryConnection=>Unit

  case class Read[T](action:Reading[T])
  case class Select[T](query:String,action:TupleQuering[T], baseUrl:String = WI.RESOURCE)
  case class Construct[T](query:String,action:GraphQuering[T], baseUrl:String = WI.RESOURCE)
  case class Update(query:String,action:UpdateQuering, baseUrl:String = WI.RESOURCE)
  case class Write(action:Writing)

}
