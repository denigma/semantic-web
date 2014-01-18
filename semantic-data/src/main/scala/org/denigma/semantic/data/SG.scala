package org.denigma.semantic.data

import java.util.Properties
import java.io._
import scala.collection.immutable.Map
import org.openrdf.model.impl.StatementImpl
import com.bigdata.rdf.internal.IV
import com.bigdata.rdf.model.BigdataValue

//import org.apache.log4j.Logger
import com.bigdata.rdf.sail.{BigdataSailRepositoryConnection, BigdataSailRepository, BigdataSail}
import scala.util.Try
import org.apache.commons.io.FileUtils
import play.api.Play
import play.api.Play.current
import org.openrdf.repository.RepositoryResult
import SG.db
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.query.{BindingSet, TupleQueryResult, QueryLanguage}
import org.openrdf.model._
import org.openrdf.model.vocabulary._
import scala.util.{Try, Success, Failure}




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
        //Resource subject, URI predicate, Value object
        //val st = v.createStatement(v.createURI("",""),v.createURI("",""),v.createURI("","")).getModified
        //val i: IV[_ <: BigdataValue, _] =   st.getStatementIdentifier

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
    res.recoverWith{case
      e=>
      play.Logger.error("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }


  /*
  writes something and then closes the connection
   */
  def readWrite[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = repo.getReadOnlyConnection
    con.setAutoCommit(false)
    val res = Try {
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
    e=>
    play.Logger.error("read/write transaction from database failed because of \n"+e.getMessage)
    res
  }
  }




  /*
  writes something and then closes the connection
   */
  def write(action:BigdataSailRepositoryConnection=>Unit):Boolean = this.readWrite[Unit](action) match
  {
    case Success(_)=>true
    case Failure(e)=>false
  }



  def lookup(str:String,predicate:String,obj:String) = this.query{
    s"""
    prefix bd: <http://www.bigdata.com/rdf/search#>
    select ?s
      where {
        ?s bd:search "${str}" .
        ?s ${predicate} ${obj} .
      }
    """
  }

  def searchTerm(str:String,minRelevance:Double=0.25,maxRank:Int=1000) = this.query{
    s"""
      prefix bd: <http://www.bigdata.com/rdf/search#>
      select ?s, ?p, ?o, ?score, ?rank
      where {
        ?o bd:search "${str}" .
        ?o bd:minRelevance "${minRelevance}" .
        ?o bd:maxRank "${maxRank}" .
      }
    """
  }

  def search(str:String,minRelevance:Double=0.25,maxRank:Int=1000) = this.query{
    s"""
      prefix bd: <http://www.bigdata.com/rdf/search#>
      select ?s, ?p, ?o, ?score, ?rank
      where {
        ?o bd:search "${str}" .
        ?o bd:matchAllTerms "true" .
        ?o bd:minRelevance "${minRelevance}" .
        ?o bd:relevance ?score .
        ?o bd:maxRank "${maxRank}" .
        ?o bd:rank ?rank .
        ?s ?p ?o .
      }
    """
  }

  def lookupTerm(str:String) = this.query{
    s"""
    prefix bd: <http://www.bigdata.com/rdf/search#>
    select ?o
      where {
        ?o bd:search "${str}" .
      }
    """
  }

  /*
  runs query over db
   */
  def query(str:String, lan: QueryLanguage= QueryLanguage.SPARQL):Try[QueryResult] = db.readWrite{
    implicit r=>
      val q = r.prepareTupleQuery(
        lan,str
      )

      val results: TupleQueryResult = q.evaluate()
      val names = results.getBindingNames.toList

      var re: List[Map[String,String]] = List.empty[Map[String,String]]
      while(results.hasNext){
        re = binding2List(names,results.next())::re
      }
      QueryResult(str,names,re.reverse)

  }//.getOrElse(QueryResult(str,List.empty[String],List.empty[Map[String,String]]))

  def binding2List(names:List[String],b:BindingSet):Map[String,String] = {
    names.map{
      case name=> (name,b.getValue(name).stringValue())
    }.toMap
  }

}
