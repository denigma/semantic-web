package org.denigma.semantic.reading

import com.bigdata.rdf.sail.BigdataSailQuery

/*
useful implicits and
 */
package object queries {

  type AnyQuery = BigdataSailQuery
  type AnyQueryHandler[T] = PartialFunction[(String,ReadConnection,AnyQuery),T]
}
