package org.denigma.semantic.simple

import com.bigdata.rdf.sail.{BigdataSailTupleQuery, BigdataSailRepositoryConnection, BigdataSailRepository, BigdataSail}
import java.io.File
import java.util.Properties
import org.apache.commons.io.FileUtils
import scala.util.Try
import org.openrdf.query.{TupleQueryResult, QueryLanguage}
import org.openrdf.model.impl.URIImpl
import org.denigma.semantic.reading._

/**
 * Factory companion object
 */
object BigData {

  /**
   * Factory
   * @param clean if true => delete database journal file (if exists)
   * @param url path to database journal
   * @param dbFileName database journal filename
   * @return
   */
  def apply(clean:Boolean,url:String="./db/test/",dbFileName:String="bigdata.jnl"): BigData = {
    if(clean) cleanLocalDb(url,dbFileName)
    new BigData(url,dbFileName)
  }


  /*
deletes local db file (used mostly in tests)
 */
  def cleanLocalDb(url:String,dbFileName:String): Boolean =  {
    val f = new File(url+"/"+dbFileName)
    if(f.exists()) {
      if(FileUtils.deleteQuietly(f)) {
        println(s"previous database was successfully deleted")
        true
      } else false
    } else false
    //FileUtils.cleanDirectory(new File(dbConfig.url))
  }
}

/**
 * Simpliest as possible BigDataSetup
 */
class BigData(url:String="./db/test/",dbFileName:String="bigdata.jnl")
{
  /**
   * Type alias for BigDataTupleQuery
   */
  type SelectQuery = BigdataSailTupleQuery

  type SelectHandler[T] = (String,ReadConnection,SelectQuery)=>T

  /**
  BigData settings
   */
  lazy val properties:Properties = {
    val props = new Properties()
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.quadsMode","true")
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex","true")
    props.setProperty("com.bigdata.rdf.sail.truthMaintenance","false")
    props.setProperty("com.bigdata.rdf.sail.statementIdentifiers","false")
    props.setProperty("com.bigdata.journal.AbstractJournal.file",url+"/"+dbFileName)
    props
  }



 /*
  initiates embeded bigdata database
   */
  val sail: BigdataSail = {
     val journal = new File(dbFileName)
      if(!journal.exists())journal.createNewFile()
      properties.setProperty(BigdataSail.Options.DEFAULT_FILE, journal.getAbsolutePath)

    new BigdataSail(properties)
  }


  /*
  Bigdata Sesame repository
   */
  lazy val repo: BigdataSailRepository = {
    val repo = new BigdataSailRepository(sail)
    repo.initialize()
    repo
  }

  def readConnection: BigdataSailRepositoryConnection = repo.getReadOnlyConnection //for convenience, provides read connection

  def writeConnection: BigdataSailRepositoryConnection = repo.getUnisolatedConnection //for convenience, WARNING 1 writer per database


  /**
   * Just does select
   * @param query query
   * @param base basic value
   * @return Try with results of query execution
   */
  def select(query:String)(implicit base:String = "http://testme.bigdata.com"): Try[TupleQueryResult] = this.read{
    con=>
      val q = con.prepareTupleQuery(QueryLanguage.SPARQL,query,base)
      q.evaluate()
  }

  /**
   * Reads something
   * @param action function that reads
   * @tparam T what it returns
   * @return Try of T
   */
  def read[T](action:Reading[T]):Try[T]= {
    val con: BigdataSailRepositoryConnection = this.readConnection
    val res = Try {
      action(con)
    }

    con.close()
    res.recoverWith{case
      e=>
      print("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }


  /**
   * Writes something and then closes the connection
   * @param action
   * @tparam T what we want to get in return
   * @return Try of T
   */
  def write[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = this.writeConnection
    con.setAutoCommit(false)
    val res = Try {
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
      e=>
      print("read/write transaction from database failed because of \n"+e.getMessage)
      res
    }
  }



  /**
  shutdowns the database
   */
  def shutDown() = this.repo.shutDown()

}