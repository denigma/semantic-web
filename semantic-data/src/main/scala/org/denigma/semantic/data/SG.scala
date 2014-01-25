package org.denigma.semantic.data

import java.util.Properties
import java.io._

//import org.apache.log4j.Logger
import com.bigdata.rdf.sail._
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.query.{TupleQuery, TupleQueryResult, QueryLanguage}
import org.openrdf.model._

import scala.util.{Try, Success, Failure}




object SG extends SemanticHelper{

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

  lazy val dbConf: Configuration =  if(Play.isTest)   conf.getConfig("repo.test").get else if(Play.isDev) conf.getConfig("repo.dev").get   else conf.getConfig("repo.prod").get

  lazy val limit: Long = this.dbConf.getLong("limit").getOrElse(50)


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
class SG(implicit val lg:org.slf4j.Logger) extends SemanticStore{



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




}
