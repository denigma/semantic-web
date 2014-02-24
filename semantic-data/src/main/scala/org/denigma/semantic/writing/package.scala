package org.denigma.semantic

import com.bigdata.rdf.sail.{BigdataSailUpdate, BigdataSailRepositoryConnection}

/*
package object with useful vals, defs, type aliases and implicits
 */
package object writing {
  type Writing[T] = BigdataSailRepositoryConnection=>T


  type UpdateQuering[T] = (String,BigdataSailRepositoryConnection,BigdataSailUpdate)=>T
}
