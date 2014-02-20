package org.denigma.semantic

import java.util.Properties
import java.io._
import org.denigma.semantic.data.SemanticStore
import java.util
import org.openrdf.repository.RepositoryResult
import org.denigma.semantic.quering.QueryWizard
import scala.util.Try
import akka.actor.{Props, ActorRef}
import org.denigma.semantic.actors.{DatabaseWriter, DatabaseReader}

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.model._
import scala.collection.immutable._
import scala.collection.JavaConversions._
import play.api.libs.concurrent.Akka
import akka.actor._
import akka.routing._

/*
class that is responsible for the main logic
 */
abstract class SemanticPlatform extends QueryWizard{

  var dbConfig:DBConfig
  var platformConfig:PlatformConfig

  type Store = SemanticStore

  var db:Store = null

  var platformParams: List[Statement] = List.empty[Statement]

  var reader: ActorRef = null

  var writer: ActorRef = null


  implicit val lg = play.api.Logger.logger

  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd


  def inTest(action: =>Unit):Unit = if(this.inTest) { action}


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
    reader = makeReader(db,sys)
    writer = makeWriter(db,sys)
    if(platformConfig.loadInitial)  this.loadInitialData()
  }

  /*
  creates readers with router
   */
  def makeReader(database:Store,sys:ActorSystem):ActorRef = {

    val router = SmallestMailboxRouter(platformConfig.defReaders)
    val resizer = DefaultResizer(lowerBound = platformConfig.minReaders, upperBound = platformConfig.maxReaders)

    val props = Props(classOf[DatabaseReader],database).withRouter(router.withResizer(resizer))

    //SmallestMailboxPool(5).props(props)
    sys.actorOf(props,"reader")
  }

  /*
  make writing actor
   */
  def makeWriter(database:Store,sys:ActorSystem): ActorRef = {
    val props = Props(classOf[DatabaseWriter],database)
    sys.actorOf(props,"writer")
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
  stops the database and semanticplatform
   */
  def stop() = {
    lg.info(db.repo.toString)
    db.shutDown()
  }


}