package org.denigma.semantic.reading

import com.bigdata.rdf.sail.BigdataSailBooleanQuery

/*
Ask query
 */
package object questions {
  type AskQuery = BigdataSailBooleanQuery

  type AskQuerying[T] = (String,ReadConnection,AskQuery)=>T

}
