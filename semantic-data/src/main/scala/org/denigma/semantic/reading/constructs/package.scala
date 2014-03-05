package org.denigma.semantic.reading

import com.bigdata.rdf.sail.BigdataSailGraphQuery
import org.openrdf.query.GraphQueryResult
import org.openrdf.model.Statement

/**
 * Construct package object with useful type aliases
 */
package object constructs {
  type ConstructHandler[T] = (String,ReadConnection,ConstructQuery)=>T

  type ConstructQuery = BigdataSailGraphQuery


  implicit class GraphResult(results:GraphQueryResult) extends Iterator[Statement]
  {
    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }
}
