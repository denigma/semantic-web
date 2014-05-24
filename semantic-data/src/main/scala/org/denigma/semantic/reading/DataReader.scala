package org.denigma.semantic.reading

import scala.util.Try
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.scalax.semweb.sesame.SesameReader

/*
trait that does read operations
 */
trait DataReader extends CanReadBigData with SesameReader