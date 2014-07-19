package org.denigma.semantic.reading

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection
import org.denigma.semantic.reading.queries.AnyQueryReader
import org.scalax.semweb.sesame.{SesameReader, CanReadSesame}
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection

/*
just a trait
 */
trait CanReadBigData extends CanReadSesame{
  type ReadConnection  = BigdataSailRepositoryConnection
}

/*
trait that does read operations
 */
trait DataReader extends CanReadBigData with SesameReader

/*
trait that provides all reading features
 */
trait UniReader extends CanReadBigData with AnyQueryReader with BigDataAskReader with DataReader with BigDataConstructReader with BigDataSelectReader{

}
