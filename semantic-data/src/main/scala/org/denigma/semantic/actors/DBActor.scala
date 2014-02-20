package org.denigma.semantic.actors

import akka.actor.Actor
import org.denigma.semantic.data.RDFStore
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.collection.immutable.{Set, Vector, Map, List}
import play.api.libs.json.{Json, JsValue, JsObject}
import scala.Predef.Set

/*
actors that contains db and can process something
 */
abstract class DBActor(val db:RDFStore) extends  NamedActor{

}

object Data{

  type reading[T] = BigdataSailRepositoryConnection=>T
  type writing = BigdataSailRepositoryConnection=>Unit

  case class Read[T](action:reading[T])
  case class Write(action:writing)
//
//
//  case class OperationResult[T](res:T)

//  case class Reader[T](action:reading[T])
//  case class Writer[T](action:writing)
//  case class ReadWriter[T](Write:T)

//
//  case class Select(query:String, contexts:Set[String])
//
//  case class Update(query:String, contexts:Set[String])
//
//  case class SelectReader() extends Reader[Select]

}
