package org.denigma.semantic.storage

import com.bigdata.rdf.sail._

import org.denigma.semantic.files.SemanticFileParser
import org.denigma.semantic.reading.queries.UniReader
import org.denigma.semantic.writing.UpdateWriter

/*class that deals with storing and retrieving RDF from bigdata storage*/
abstract class RDFStore extends UniReader with UpdateWriter with SemanticFileParser{

  val repo: BigdataSailRepository

  def readConnection: BigdataSailRepositoryConnection = repo.getReadOnlyConnection
  def writeConnection: BigdataSailRepositoryConnection = repo.getUnisolatedConnection

  /*
 Shutdown repository
  */
  def close()=repo.shutDown()

}


