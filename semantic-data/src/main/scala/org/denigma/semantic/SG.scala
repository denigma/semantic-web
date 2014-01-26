package org.denigma.semantic

import java.util.Properties
import java.io._
import org.denigma.semantic.data.{QueryResult, SemanticHelper, SemanticStore}
import org.denigma.semantic.classes.SpinManager

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.query.QueryLanguage
import org.openrdf.model._

import scala.util.{Try, Failure}




object SG extends SemanticHelper{

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd




  def inTest(action: =>Unit):Unit = if(this.inTest)  action

  type Store = SG

  var db:Store = null

  lazy val url:String = dbConf.getString("url").get
  lazy val name = dbConf.getString("name").getOrElse("bigdata.jnl")

  lazy val dbConf: Configuration =  if(Play.isTest)   conf.getConfig("repo.test").get else if(Play.isDev) conf.getConfig("repo.dev").get   else conf.getConfig("repo.prod").get
  lazy val truthMaintenance: Boolean = this.dbConf.getBoolean("com.bigdata.rdf.sail.truthMaintenance").getOrElse(false)
  lazy val storeConf = this.dbConf.getConfig("com.bigdata.rdf.store.AbstractTripleStore").get
  lazy val quads = storeConf.getBoolean("quadsMode").getOrElse(false)
  lazy val textIndex = storeConf.getBoolean("textIndex").getOrElse(true)



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
class SG(implicit val lg:org.slf4j.Logger) extends SemanticStore with SpinManager{



  val dbFileName = "bigdata.jnl"


  /*
  BigData settings
  TODO: load from play config
   */
  lazy val properties:Properties = {
    val props = new Properties()
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.quadsMode",SG.quads.toString)
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex",SG.textIndex.toString)
    props.setProperty("com.bigdata.rdf.sail.truthMaintenance",SG.truthMaintenance.toString)
    props.setProperty("com.bigdata.journal.AbstractJournal.file",SG.url+"/"+SG.name)
    //props.setProperty("com.bigdata.journal.AbstractJournal.initialExtent","209715200")
    //props.setProperty("com.bigdata.journal.AbstractJournal.maximumExtent","209715200")
    props
  }

  def lookup(str:String,predicate:String,obj:String): Try[QueryResult] = this.query{
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
