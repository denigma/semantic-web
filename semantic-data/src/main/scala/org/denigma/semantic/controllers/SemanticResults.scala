package org.denigma.semantic.controllers

import org.openrdf.repository.RepositoryResult
import org.openrdf.model.{Value, Statement}
import com.bigdata.rdf.sail._
import org.openrdf.query.{BindingSet, GraphQueryResult, TupleQueryResult}
import scala.collection.JavaConversions._
import scala.collection.immutable._

/**
 * Created by antonkulaga on 2/23/14.
 */
object SemanticResults {
  /*
  implicit class for Repository results that adds some nice features there
   */
  implicit class StatementsResult(results:RepositoryResult[Statement]) extends Iterator[Statement]{

    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }

  implicit class TupleResult(results: TupleQueryResult)  extends Iterator[BindingSet]
  {

    def vars: List[String] = results.getBindingNames.toList

    def binding2Map(b:BindingSet): Map[String, Value] = b.iterator().map(v=>v.getName->v.getValue).toMap

    def toListMap: List[Map[String, Value]] = this.map(v=>binding2Map(v)).toList


    override def next(): BindingSet = results.next()

    override def hasNext: Boolean = results.hasNext
  }




  implicit class GraphResult(results:GraphQueryResult) extends Iterator[Statement]
  {
    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }

}
