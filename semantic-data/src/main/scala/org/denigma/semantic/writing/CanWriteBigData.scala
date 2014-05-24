package org.denigma.semantic.writing

import org.scalax.semweb.sesame.CanWriteSesame
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

/**
Trait that can provide writeConnection. It is used everywhere where we need to write something into the database
 */
trait CanWriteBigData extends CanWriteSesame{

  type WriteConnection  = BigdataSailRepositoryConnection
}
