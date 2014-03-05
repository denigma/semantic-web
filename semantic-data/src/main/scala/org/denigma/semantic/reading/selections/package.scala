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

  type SelectHandler[T] = (String,ReadConnection,SelectQuery)=>T

  /**
   * Implicit class that turns  query result into iterator (so methods toList, map and so on can be applied to it)
   * @param results
   */
  implicit class TupleResult(results: TupleQueryResult)  extends Iterator[BindingSet]
  {

    lazy val vars: List[String] = results.getBindingNames.toList

    def binding2Map(b:BindingSet): Map[String, Value] = b.iterator().map(v=>v.getName->v.getValue).toMap

    lazy val toListMap: List[Map[String, Value]] = this.map(v=>binding2Map(v)).toList


    override def next(): BindingSet = results.next()

    override def hasNext: Boolean = results.hasNext
  }
}
