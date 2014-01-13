package org.denigma.semantic.data

import java.util.Properties
import java.io._
import scala.collection.immutable.List

//import org.apache.log4j.Logger
import com.bigdata.rdf.sail.{BigdataSailRepositoryConnection, BigdataSailRepository, BigdataSail}
import org.openrdf.model.impl.{StatementImpl, URIImpl}
import scala.util.Try
import org.openrdf.repository.RepositoryResult
import org.openrdf.model._
import org.apache.commons.io.FileUtils
import play.api.Play
import play.api.Play.current
import scala.collection.JavaConversions._



object SG{

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd


  def inTest(action: =>Unit):Unit = if(this.inTest)  action


  var db:SG = null

  lazy val url:String = if(Play.isTest)
    conf.getString("repo.test.url").get
    else if(Play.isDev) conf.getString("repo.dev.url").get
    else conf.getString("repo.prod.url").get

  def cleanLocalDb()=  FileUtils.cleanDirectory(new File(url))

  def cleanIfInTest() = this.inTest{
    this.cleanLocalDb()
  }
  def conf =  Play.current.configuration

  /*
 reads relationship from the repository
  */
  def withRel(rel:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,rel,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }


  def withSubject(sub:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(sub,null,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }


  def withObject(obj:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,null,obj,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  def withSubRel(sub:URI,rel:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(sub,rel,null,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  def withRelObj(rel:URI,obj:URI,inferred:Boolean=true) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,rel,obj,inferred)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  implicit class MagicUri(uri:URI) {

    def ~>(inferred:Boolean=true) = withSubject(uri,inferred)
    def <~(inferred:Boolean=true) = withObject(uri,inferred)
    def <~(rel:URI) = withRelObj(rel,uri)
    def ~>(rel:URI) = withSubRel(uri,rel)



  }

}




/**
 * Created by antonkulaga on 1/12/14.
 */
class SG(implicit lg:org.slf4j.Logger)  {

  val dbFileName = "bigdata.jnl"

  /*
  BigData settings
  TODO: load from play config
   */
  lazy val properties:Properties = {
    val props = new Properties()
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.statementIdentifiers","true")
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex","true")
    props.setProperty("com.bigdata.journal.AbstractJournal.file",SG.url+"/bigdata.jnl")
    //props.setProperty("com.bigdata.journal.AbstractJournal.initialExtent","209715200")
    //props.setProperty("com.bigdata.journal.AbstractJournal.maximumExtent","209715200")
    props
  }


  val sail: BigdataSail = {
    //val log =  Logger.getLogger(classOf[BigdataSail])

    // create a backing file for the database
    //val journal = File.createTempFile("bigdata", ".jnl")

    if (properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
      val journal = new File(this.dbFileName)
      if(!journal.exists())journal.createNewFile()

      //val oFile = new FileOutputStream(journal, false)

      //log.info(journal.getAbsolutePath)
      properties.setProperty(BigdataSail.Options.DEFAULT_FILE, journal.getAbsolutePath)
    }


    new BigdataSail(properties)
  }

  /*
  Bigdata Sesame repository
   */
  lazy val repo = {
    val repo = new BigdataSailRepository(sail)
    repo.initialize()
    repo
  }

  /*
  Shutdown repository
   */
  def close()={
    repo.shutDown()
  }

  /*
  does something with Sesame connection and then closes it
   */
  def withConnection(con:BigdataSailRepositoryConnection)(action:BigdataSailRepositoryConnection=>Unit) = {
    if(!con.isReadOnly) con.setAutoCommit(false)
    Try {
      action(con)
      if(!con.isReadOnly) con.commit()
    }.recover{case f=>lg.error(f.toString)}
    lg.debug("operation successful")
    con.close()
  }

  /*
  reads from bigdata
   */
  def read[T](action:BigdataSailRepositoryConnection=>T):Try[T]= {
    val con = repo.getReadOnlyConnection
    val res = Try {
      action(con)
    }
    con.close()
    res
  }




  /*
  writes something and then closes the connection
   */
  def write(action:BigdataSailRepositoryConnection=>Unit):Boolean =
  {
    val con = repo.getConnection
    con.setAutoCommit(false)
    Try {
      action(con)
      con.commit()
      con.close()
      true
    }.getOrElse{
        lg.error(s"operation failed")
        con.close()
        false
    }
  }


}
