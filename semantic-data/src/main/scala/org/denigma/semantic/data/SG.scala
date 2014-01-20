package org.denigma.semantic.data

import java.util.Properties
import java.io._

//import org.apache.log4j.Logger
import com.bigdata.rdf.sail._
import org.apache.commons.io.FileUtils
import play.api.Play
import play.api.Play.current
import SG.db
import org.openrdf.query.{TupleQuery, TupleQueryResult, QueryLanguage}
import org.openrdf.model._

import scala.util.{Try, Success, Failure}




object SG extends SemanticQuery{

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd


  def inTest(action: =>Unit):Unit = if(this.inTest)  action

  type Store = SG


  var db:Store = null

  lazy val url:String = if(Play.isTest)
      conf.getString("repo.test.url").get
    else if(Play.isDev) conf.getString("repo.dev.url").get
    else conf.getString("repo.prod.url").get

  def cleanLocalDb()=  FileUtils.cleanDirectory(new File(url))

  def cleanIfInTest() = this.inTest{
    this.cleanLocalDb()
  }
  def conf =  Play.current.configuration





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
class SG(implicit lg:org.slf4j.Logger) extends SemanticStore{

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
  lazy val repo: BigdataSailRepository = {
    val repo = new BigdataSailRepository(sail)
    repo.initialize()
    repo
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
      val q: TupleQuery = r.prepareTupleQuery(
        lan,str
      )
      val results: TupleQueryResult = q.evaluate()
      QueryResult.parse(str,results)


  }//.getOrElse(QueryResult(str,List.empty[String],List.empty[Map[String,String]]))



}
