package org.denigma.semantic.actors

import akka.actor.Actor
import org.denigma.semantic.data._
import com.bigdata.rdf.sail.{BigdataSailQuery, BigdataSailRepositoryConnection}
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

  case class Ask[T](query:String,action:AskQuering[T], baseUrl:String = WI.RESOURCE)


  /*
  When we do not know if it will ba ask, select or construct
   */
  case class AnyQuery[T](query:String,actions:AnyQuering[T], baseUrl:String = WI.RESOURCE)


  case class Update[T](query:String,action:UpdateQuering[T], baseUrl:String = WI.RESOURCE)


  case class Write[T](action:Writing[T])

}
