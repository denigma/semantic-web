package org.denigma.semantic

import com.bigdata.rdf.sail._

/**
 * Created by antonkulaga on 2/21/14.
 */
package object data {
  type Reading[T] = BigdataSailRepositoryConnection=>T

  type Writing[T] = BigdataSailRepositoryConnection=>T

  type TupleQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailTupleQuery)=>T

  type AskQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailBooleanQuery)=>T

  type GraphQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailGraphQuery)=>T

  type AnyQuering[T] = PartialFunction[(String,BigdataSailRepositoryConnection,BigdataSailQuery),T]

  type UpdateQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailUpdate)=>T
}
