package org.denigma.semantic.storage

import com.bigdata.rdf.sail._
import java.io.File
import org.{openrdf=>se}
import org.denigma.semantic.reading.queries.{JsonQueryManager, QueryManager}
import org.slf4j.Logger
import org.denigma.semantic.storage.DBConfig


/*
Wrapper for the dabase
 */
class SemanticStore(val conf:DBConfig,val lg:org.slf4j.Logger) extends RDFStore{
  self=>

  val sail: BigdataSail = {
    if (conf.properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
      val journal = new File(conf.dbFileName)
      if(!journal.exists())journal.createNewFile()
      conf.properties.setProperty(BigdataSail.Options.DEFAULT_FILE, journal.getAbsolutePath)
    }
    new BigdataSail(conf.properties)
  }

  val url: String = conf.url


  /*
  Bigdata Sesame repository
   */
  lazy val repo: BigdataSailRepository = {
    val repo = new BigdataSailRepository(sail)
    repo.initialize()
    repo
  }

  /*
  shutdowns the database
   */
  def shutDown() = this.repo.shutDown()

}