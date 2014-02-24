package org.denigma.semantic

import com.bigdata.rdf.sail.{BigdataSailUpdate, BigdataSailRepositoryConnection}
import org.denigma.semantic.quering.QueryResultLike
import akka.util.Timeout
import org.openrdf.query.{GraphQueryResult, BindingSet, TupleQueryResult, QueryLanguage}
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.{Value, Statement}
import scala.collection.immutable.{Map, List}
import scala.collection.JavaConversions._
import org.denigma.semantic.reading.QueryResultLike

/**
 * Created by antonkulaga on 2/23/14.
 */
package object controllers {

  type Selection = BigdataSailRepositoryConnection=>QueryResultLike
  type Update = BigdataSailUpdate


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
