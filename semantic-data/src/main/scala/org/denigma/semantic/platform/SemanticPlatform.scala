package org.denigma.semantic.platform

import java.io._
import org.scalax.semweb.rdf.vocabulary._
import org.openrdf.repository.RepositoryResult
import scala.util.Try
import org.denigma.semantic.actors.DatabaseActorsFactory
import org.denigma.semantic.storage.{DBConfig, SemanticStore}
import org.denigma.semantic.controllers.{LoggerProvider, UpdateController, JsQueryController}
import org.denigma.semantic.controllers.sync.{SyncWriter, SyncReader}
import org.denigma.semantic.users.Accounts
import org.denigma.semantic.schema.Schema
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.Quad


//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.Configuration
import org.openrdf.model._
import scala.collection.immutable._
import scala.collection.JavaConversions._
import play.api.libs.concurrent.Akka
import org.scalax.semweb.sesame._

/*
class that is responsible for the main logic
 */
abstract class SemanticPlatform extends JsQueryController with UpdateController with AppConfig{
  self=>

 // lg.debug("PLATFORM WAS CREATED FIRES!")

  var dbConfig:DBConfig
  var platformConfig:PlatformConfig

  type Store = SemanticStore

  var db:Store = null

  /*
  app config
   */
  var conf:Configuration

  var platformParams: List[Quad] = List.empty[Quad]

  var databaseActorsFactory:DatabaseActorsFactory = null


  def inTest(action: =>Unit):Unit = if(this.inTest) { action}

  /*
  extracts application configuration
   */
  def extractConfig(app: play.api.Application) = {

    val playConf:Configuration = app.configuration

    Try {
      conf= this.currentConfig(playConf)(app)// if(Play.isTest(app))  playConf.getConfig("test").get else if(Play.isDev(app)) playConf.getConfig("dev").get   else playConf.getConfig("prod").get
      dbConfig = new DBConfig(conf.getConfig("repo").get)
      platformConfig = new PlatformConfig(conf)
    }.recover{case e=>
      lg.error(s"error in extracting config ${e.toString}")
      throw playConf.globalError(s"Configuration of SemanticPlugin is wrong!")
    }
  }


  /*
  deletes local db file (used mostly in tests)
   */
  def cleanLocalDb()=  {
    lg.info("cleaning local db...")
    val f = new File(dbConfig.url+"/"+dbConfig.dbFileName)
    if(f.exists()) {
      if(FileUtils.deleteQuietly(f)) lg.debug("cleaning db files...")

    }
    //FileUtils.cleanDirectory(new File(dbConfig.url))
  }

  /**
  cleans db file if app is run in a test mode
   */
  def cleanIfInTest() = if(this.inTest){
    this.cleanLocalDb()
  }

  /*
  starts SemanticData plugin
   */
  def start(app: play.api.Application) = {

    //lg.debug("START PLUGIN EVENT!")
    this.db = new Store(dbConfig,lg)
    LoggerProvider.lg = this.lg
    SyncReader.reader = this.db
    SyncWriter.writer = this.db

    val sys = Akka.system(app)
    //TODO: refactor constructore of actor factory
    this.databaseActorsFactory = new DatabaseActorsFactory(db.db,db,db,sys,
      (platformConfig.minReaders,platformConfig.defReaders,platformConfig.maxReaders)
    )
    if(platformConfig.loadInitial)  this.loadInitialData()
    Accounts.activate()
    //Schema.activate()
  }


  /**
   * Loads platform settings
   * @return
   */
  def loadPlatform() = {
    this.platformParams = db.read{
    implicit r=>
      val f = r.getValueFactory
      //val pl = f.createURI(platformConfig.CONFIG_CONTEXT+"Current_Platform")
      val cont = f.createURI(platformConfig.CONFIG_CONTEXT)
      val pl = WI.PLATFORM / "Current_Platform" iri
      val iter: RepositoryResult[Statement] = r.getStatements( pl, null,null,true,cont)
      iter.toQuadList
  }.getOrElse(List.empty[Quad])
    this.platformParams
  }




  /*
  Loads turtles with further configurations and vocabularies
   */
  def loadInitialData() ={
    this.loadPlatform()
    if(this.platformParams.isEmpty) Try {
      this.loadFiles(platformConfig.filesConf)
    }.recover{case e=>
      lg.error(s"error in loading files ${e.toString}")
    }
  }
  /*
  load files form the config
   */
  def loadFiles(files:List[Configuration]) = {

    files.foreach{f=>
      (f.getString("folder") , f.getString("name")) match {
        case (Some(folder),Some(fileName))=> db.parseFileByName(folder+fileName,f.getString("context").getOrElse(WI.RESOURCE))
        case tuple => this.db.lg.error(s"invalid file params in config: ${tuple.toString()}")
      }

    }
  }


  /*
  stops the database and semanticplatform
   */
  def stop() = {
    lg.info(db.repo.toString)
    db.shutDown()
  }


}