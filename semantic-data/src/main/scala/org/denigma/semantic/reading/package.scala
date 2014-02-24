package org.denigma.semantic

import com.bigdata.rdf.sail._
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.{Value, Statement}
import org.openrdf.query.{GraphQueryResult, BindingSet, TupleQueryResult}
import scala.collection.immutable.{Map, List}
import scala.collection.JavaConversions._

/*
package object that stores useful type aliases, implicits, vals and defs for reading package
 */
package object reading {

  type ReadConnection  = BigdataSailRepositoryConnection

  type Reading[T] = ReadConnection=>T

  /*
implicit class for Repository results that adds some nice features there and turnes it into Scala Iterator
 */
  implicit class StatementsResult(results:RepositoryResult[Statement]) extends Iterator[Statement]{

    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }






}
