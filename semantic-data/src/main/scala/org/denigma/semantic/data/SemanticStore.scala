package org.denigma.semantic.data

import com.bigdata.rdf.sail._
import java.io.File
import org.{openrdf=>se}
import org.denigma.semantic.DBConfig


/*
Wrapper for the dabase
 */
class SemanticStore(val conf:DBConfig,val lg:org.slf4j.Logger) extends RDFStore{

  val sail: BigdataSail = {
    //val log =  Logger.getLogger(classOf[BigdataSail])

    // create a backing file for the database
    //val journal = File.createTempFile("bigdata", ".jnl")

    if (conf.properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
      val journal = new File(conf.dbFileName)
      if(!journal.exists())journal.createNewFile()

      //val oFile = new FileOutputStream(journal, false)

      //log.info(journal.getAbsolutePath)
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