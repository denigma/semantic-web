package org.denigma.semantic.reading

import com.bigdata.rdf.sail.BigdataSailTupleQuery
import org.openrdf.query.{BindingSet, TupleQueryResult}
import scala.collection.immutable.{Map, List}
import scala.collection.JavaConversions._
import org.openrdf.model.Value

/*
type aliases and implicit classes for select package
 */
package object selections {


  type SelectQuery = BigdataSailTupleQuery

  type SelectQuerying[T] = (String,ReadConnection,SelectQuery)=>T

  implicit class TupleResult(results: TupleQueryResult)  extends Iterator[BindingSet]
  {

    def vars: List[String] = results.getBindingNames.toList

    def binding2Map(b:BindingSet): Map[String, Value] = b.iterator().map(v=>v.getName->v.getValue).toMap

    def toListMap: List[Map[String, Value]] = this.map(v=>binding2Map(v)).toList


    override def next(): BindingSet = results.next()

    override def hasNext: Boolean = results.hasNext
  }
}
