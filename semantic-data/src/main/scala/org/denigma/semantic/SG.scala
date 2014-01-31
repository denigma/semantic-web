package org.denigma.semantic

import java.util.Properties
import java.io._
import org.denigma.semantic.data.{QueryWizard, QueryResult, SemanticHelper, SemanticStore}
import org.denigma.semantic.classes.SpinManager
import com.hp.hpl.jena.query.Query
import org.denigma.semantic.Prefixes.WI

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.query.QueryLanguage
import org.openrdf.model._



import scala.util.{Try, Failure}

/*
object (or static class) that is contains the database (to make sure that it is only one per app)
 */
object SG extends SemanticHelper with QueryWizard{

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd



  def inTest(action: =>Unit):Unit = if(this.inTest)  action

  type Store = SG

  var db:Store = null

  lazy val url:String = dbConf.getString("url").get
  lazy val name = dbConf.getString("name").getOrElse("bigdata.jnl")

  /*
  whole play configuration, including production all modes
   */
  def playConf =  Play.current.configuration

  /*
  configuration of a current mode
   */
  lazy val conf: Configuration =  if(Play.isTest)   playConf.getConfig("test").get else if(Play.isDev) playConf.getConfig("dev").get   else playConf.getConfig("prod").get


  lazy val dbConf: Configuration =  conf.getConfig("repo").get

  lazy val truthMaintenance: Boolean = this.dbConf.getBoolean("com.bigdata.rdf.sail.truthMaintenance").getOrElse(false)
  lazy val storeConf = this.dbConf.getConfig("com.bigdata.rdf.store.AbstractTripleStore").get
  lazy val quads = storeConf.getBoolean("quadsMode").getOrElse(false)
  lazy val textIndex = storeConf.getBoolean("textIndex").getOrElse(true)
  lazy val filesConf = conf.getConfigList("files").getOrElse(Nil)
  //lazy val semanticConf = conf.getConfig("semantic")




  lazy val limit: Long = this.dbConf.getLong("limit").getOrElse(50)



  def cleanLocalDb()=  FileUtils.cleanDirectory(new File(url))

  def cleanIfInTest() = this.inTest{
    this.cleanLocalDb()
  }

  def initPrefixes = {

    val root = "Web_Intelligence"
    WI.root
  }



}




/*
this class is class of database wrapper used by playapp
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
