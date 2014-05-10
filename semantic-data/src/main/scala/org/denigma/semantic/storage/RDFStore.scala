package org.denigma.semantic.storage

import com.bigdata.rdf.sail._

import org.denigma.semantic.files.SemanticFileParser
import org.denigma.semantic.reading.queries.UniReader
import org.denigma.semantic.writing.DataWriter
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository

/**
* abstract class that deals with storing and retrieving RDF
* for practical implementation look for [[SemanticStore]] instead
* */
abstract class RDFStore extends UniReader
with DataWriter with SemanticFileParser //some traits with useful methods
{

  val repo: BigdataSailRepository

  def readConnection: BigdataSailRepositoryConnection = repo.getReadOnlyConnection //needed for CanRead trait

  def writeConnection: BigdataSailRepositoryConnection = repo.getUnisolatedConnection //needed for CanWrite trait



  /*
 Shutdown the repository
  */
  def close()=repo.shutDown()

}

