package org.denigma.semantic.actors

import akka.actor.Actor
import com.bigdata.rdf.sail.BigdataSailQuery
import scala.collection.immutable.List
import play.api.libs.json.JsValue
import scala.Predef.Set
import org.denigma.semantic.storage.RDFStore

/*
actors that contains db and can process something
 */
abstract class DBActor(val db:RDFStore) extends  NamedActor{

}

object Data{
////
////  type Reading[T] = BigdataSailRepositoryConnection=>T
////  type Writing = BigdataSailRepositoryConnection=>Unit
//
//  case class Read[T](action:Reading[T])
//
//  case class Select[T](query:String,action:TupleQuering[T], baseUrl:String = WI.RESOURCE)
//
//  case class Construct[T](query:String,action:GraphQuering[T], baseUrl:String = WI.RESOURCE)
//
//  case class Ask[T](query:String,action:AskQuering[T], baseUrl:String = WI.RESOURCE)
//
//
//  /*
//  When we do not know if it will ba ask, select or construct
//   */
//  case class AnyQuery[T](query:String,actions:AnyQuering[T], baseUrl:String = WI.RESOURCE)
//
//
//  case class Update[T](query:String,action:UpdateQuering[T], baseUrl:String = WI.RESOURCE)
//
//
//  case class Write[T](action:Writing[T])

}
