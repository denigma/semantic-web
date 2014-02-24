package org.denigma.semantic.platform

import java.util.Properties
import java.io._
import org.openrdf.repository.RepositoryResult
import scala.util.Try
import akka.actor.ActorRef
import org.denigma.semantic.actors.{DatabaseActorsFactory, DatabaseWriter}
import org.denigma.semantic.controllers.SemanticController
import org.denigma.semantic.storage.{DBConfig, SemanticStore}
import org.denigma.semantic.reading.queries.Paginator
import org.slf4j.Logger
import org.denigma.semantic.commons.WI

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.model._
import scala.collection.immutable._
import scala.collection.JavaConversions._
import play.api.libs.concurrent.Akka
import akka.routing._

/*
class that is responsible for the main logic
 */
abstract class SemanticPlatform extends SemanticController{
  self=>

  var dbConfig:DBConfig
  var platformConfig:PlatformConfig

  type Store = SemanticStore

  var db:Store = null

  /*
  app config
   */
  var conf:Configuration

  var platformParams: List[Statement] = List.empty[Statement]

  var databaseActorsFactory:DatabaseActorsFactory[Store] = null

  implicit val lg = play.api.Logger.logger

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd


  def inTest(action: =>Unit):Unit = if(this.inTest) { action}

  def extractConfig(app: play.api.Application) = {

    val playConf:Configuration = app.configuration

    Try {
      conf=  if(Play.isTest(app))  playConf.getConfig("test").get else if(Play.isDev(app)) playConf.getConfig("dev").get   else playConf.getConfig("prod").get
      dbConfig = new DBConfig(conf.getConfig("repo").get)
      platformConfig = new PlatformConfig(conf)
    }.recover{case e=>
      lg.error(s"error in extracting config ${e.toString}")
      throw playConf.globalError(s"Configuration of SemanticPlugin is wrong!")
    }
  }


  def cleanLocalDb()=  {
    lg.info("cleaning local db...")
    val f = new File(dbConfig.url+"/"+dbConfig.dbFileName)
    if(f.exists()) {
      if(FileUtils.deleteQuietly(f)) lg.debug("cleaning db files...")

    }
    //FileUtils.cleanDirectory(new File(dbConfig.url))
  }

  def cleanIfInTest() = if(this.inTest){
    this.cleanLocalDb()
  }

  def start(app: play.api.Application,lg:org.slf4j.Logger) = {
    this.db = new Store(dbConfig,lg)
    val sys = Akka.system(app)
    this.databaseActorsFactory = new DatabaseActorsFactory[Store](db,sys,(platformConfig.minReaders,platformConfig.defReaders,platformConfig.maxReaders))
    if(platformConfig.loadInitial)  this.loadInitialData()
  }



  /*
  loads configs of the platform
   */
  def loadPlatform(): List[Statement] = {
    this.platformParams = db.read{
    implicit r=>
      val f = r.getValueFactory
      val pl = f.createURI(platformConfig.CONFIG_CONTEXT+"Current_Platform")
      val cont = f.createURI(platformConfig.CONFIG_CONTEXT)
      val iter: RepositoryResult[Statement] = r.getStatements(pl,null,null,true,cont)

      iter.asList().toList
  }.getOrElse(List.empty[Statement])
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
  def loadFiles(files: scala.List[Configuration]) = {


    files.foreach{f=>
      (f.getString("folder") , f.getString("name")) match {
        case (Some(folder),Some(fileName))=> db.parseFile(folder+fileName,f.getString("context").getOrElse(WI.RESOURCE))
        case tuple => this.db.lg.error(s"invalid file params in config: ${tuple.toString()}")
      }

    }
  }


  /*
  class to do JSON quries
   */
  object js extends Paginator{
    override def lg: Logger = self.lg

    override def readConnection: _root_.org.denigma.semantic.reading.ReadConnection = self.db.readConnection
  }


  /*
  stops the database and semanticplatform
   */
  def stop() = {
    lg.info(db.repo.toString)
    db.shutDown()
  }


}