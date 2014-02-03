package org.denigma.semantic

import java.util.Properties
import java.io._
import org.denigma.semantic.data.{QueryWizard, QueryResult, SemanticHelper, SemanticStore}
import org.denigma.semantic.classes.SpinManager
import java.util
import org.openrdf.repository.RepositoryResult
import com.bigdata.rdf.model.BigdataURI

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.model._
import scala.collection.immutable._
import scala.collection.JavaConversions._
import org.openrdf.model.impl._


import scala.util.Try


object Config {
  /*
  configuration of a current mode
   */
  lazy val conf: Configuration =  if(Play.isTest)   playConf.getConfig("test").get else if(Play.isDev) playConf.getConfig("dev").get   else playConf.getConfig("prod").get


  lazy val dbConf: Configuration =  conf.getConfig("repo").get

  lazy val truthMaintenance: Boolean = this.dbConf.getBoolean("com.bigdata.rdf.sail.truthMaintenance").getOrElse(false)
  lazy val storeConf = this.dbConf.getConfig("com.bigdata.rdf.store.AbstractTripleStore").get
  lazy val quads = storeConf.getBoolean("quadsMode").getOrElse(false)
  lazy val textIndex = storeConf.getBoolean("textIndex").getOrElse(true)
  lazy val filesConf: util.List[Configuration] = conf.getConfigList("files").get//.getOrElse(Nil)
  //lazy val semanticConf = conf.getConfig("semantic")

  lazy val CONFIG_CONTEXT = conf.getString("config_context").getOrElse(WI.CONFIG)
  lazy val MAIN_CONTEXT =  conf.getString("main_context").getOrElse(WI.RESOURCE)
  lazy val POLICY_CONTEXT =  conf.getString("main_context").getOrElse(WI.POLICY)

  lazy val limit: Long = this.dbConf.getLong("limit").getOrElse(50)

  lazy val url:String = dbConf.getString("url").get
  lazy val name = dbConf.getString("name").getOrElse("bigdata.jnl")

  lazy val loadInitial = conf.getBoolean("load_initial").getOrElse(true)

  /*
 whole play configuration, including production all modes
  */
  def playConf =  Play.current.configuration
}

/*
object (or static class) that is contains the database (to make sure that it is only one per app)
 */
object SG extends SemanticHelper with QueryWizard{

  implicit val lg = play.api.Logger.logger

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd



  def inTest(action: =>Unit):Unit = if(this.inTest) { action}

  type Store = SG

  var db:Store = null

  var platformParams: List[Statement] = List.empty[Statement]



  def cleanLocalDb()=  {
    lg.info("cleaning local db...")
    FileUtils.cleanDirectory(new File(Config.url))
  }

  def cleanIfInTest() = if(this.inTest){
    this.cleanLocalDb()
  }

  def start() = {
    if(Config.loadInitial)  this.loadInitialData()
  }

  def loadInitialData() ={
    this.platformParams =   db.read{
      implicit r=>
        val f = r.getValueFactory
        val pl = f.createURI(Config.CONFIG_CONTEXT+"Current_Platform")
        val cont = f.createURI(Config.CONFIG_CONTEXT)
        val iter: RepositoryResult[Statement] = r.getStatements(pl,null,null,true,cont)

      iter.asList().toList
    }.getOrElse(List.empty[Statement])
    if(this.platformParams.isEmpty) this.loadFiles()
  }


  def loadFiles() = {
   val iter =   Config.filesConf.iterator()
    while(iter.hasNext){
      val f: Configuration = iter.next()

      (f.getString("folder") , f.getString("name")) match {
        case (Some(folder),Some(fileName))=> db.parseFile(folder+fileName,f.getString("context").getOrElse(WI.RESOURCE))
        case tuple => this.db.lg.error(s"invalid file params in config: ${tuple.toString()}")
      }
//      {
//        folder: "data/denigma/",
//        type: "fixture",
//        name:"Cancer_ontology.ttl",
//        context: "http://denigma.org/resource/"
//      },

    }
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
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.quadsMode",Config.quads.toString)
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex",Config.textIndex.toString)
    props.setProperty("com.bigdata.rdf.sail.truthMaintenance",Config.truthMaintenance.toString)
    props.setProperty("com.bigdata.journal.AbstractJournal.file",Config.url+"/"+Config.name)
    //props.setProperty("com.bigdata.journal.AbstractJournal.initialExtent","209715200")
    //props.setProperty("com.bigdata.journal.AbstractJournal.maximumExtent","209715200")
    props
  }





}
