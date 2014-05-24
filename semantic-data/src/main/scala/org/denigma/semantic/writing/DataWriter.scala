package org.denigma.semantic.writing

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.util.Try
import org.scalax.semweb.sesame.SesameDataWriter


/*
interface for data writing
 */
trait DataWriter extends SesameDataWriter with CanWriteBigData