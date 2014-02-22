package org.denigma.semantic

import com.bigdata.rdf.sail._

/**
 * Created by antonkulaga on 2/21/14.
 */
package object data {
  type Reading[T] = BigdataSailRepositoryConnection=>T
  type Writing = BigdataSailRepositoryConnection=>Unit
  type TupleQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailTupleQuery)=>T
  type AskQuering = (String,BigdataSailRepositoryConnection,BigdataSailBooleanQuery)=>Boolean
  type GraphQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailGraphQuery)=>T
  type AnyQuering[T] = PartialFunction[(String,BigdataSailRepositoryConnection,BigdataSailQuery),T]

  type UpdateQuering = (String,BigdataSailRepositoryConnection,BigdataSailUpdate)=>Unit
}
