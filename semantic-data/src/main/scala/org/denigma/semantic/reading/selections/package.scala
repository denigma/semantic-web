package org.denigma.semantic.reading

import com.bigdata.rdf.sail.BigdataSailTupleQuery

/*
type aliases and implicit classes for select package
 */
package object selections {


  type SelectQuery = BigdataSailTupleQuery

  type SelectHandler[T] = (String,ReadConnection,SelectQuery)=>T

}
