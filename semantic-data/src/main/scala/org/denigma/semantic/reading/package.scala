package org.denigma.semantic

import com.bigdata.rdf.sail._

/*
package object that stores useful type aliases, implicits, vals and defs for reading package
 */
package object reading {

  type ReadConnection  = BigdataSailRepositoryConnection

  type Reading[T] = ReadConnection=>T


}
