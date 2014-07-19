package org.denigma.semantic.remote

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection
import org.denigma.semantic.reading.{BigDataSelectReader, BigDataConstructReader, BigDataAskReader}
import org.denigma.semantic.reading.queries.AnyQueryReader
import org.scalax.semweb.sesame.{SesameReader, CanReadSesame}



trait CanReadRemoteBigData extends CanReadSesame{
  type ReadConnection = BigdataSailRemoteRepositoryConnection

}


/*
trait that does read operations
 */
trait DataReader extends CanReadRemoteBigData with SesameReader

/*
trait that provides all reading features
 */
trait UniReader extends CanReadRemoteBigData with BigDataRemoteSelectReader{

}
