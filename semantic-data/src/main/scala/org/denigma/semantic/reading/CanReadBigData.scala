package org.denigma.semantic.reading

import org.scalax.semweb.sesame.CanReadSesame
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

/*
just a trait
 */
trait CanReadBigData extends CanReadSesame{
  type ReadConnection  = BigdataSailRepositoryConnection
}
