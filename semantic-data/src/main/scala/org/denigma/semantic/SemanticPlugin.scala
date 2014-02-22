package org.denigma.semantic

import play.api.{Play, Configuration, PlayException, Plugin}
import play.api.libs.concurrent.Akka
import java.util.Properties
import org.denigma.semantic.data.SemanticStore
import scala.util.Try

/*
Plugin that runs bigdata database and other semanticdb stuff
 */
class SemanticPlugin(app: play.api.Application) extends Plugin with WithSemanticPlatform{

  implicit val lg = play.api.Logger.logger

  override def onStart(){
    lg.debug("Starting  BigData plugin")

    SP.extractConfig(app)
    SP.cleanIfInTest()
    //SPINModuleRegistry.get.init()
    SP.start(app, play.api.Logger.logger)
  }

  override def onStop(){
    lg.debug("Stopping BigData plugin")
    SP.stop()

  }
}

/*
Semantic Platform (SP) - Object that contains db, actors and so on
 */
object SP extends SemanticPlatform{

  var conf:Configuration  = null

  var platformConfig: PlatformConfig = null
  var dbConfig: DBConfig = null


  /** Returns the current instance of the plugin. */
  def current(implicit app:  play.api.Application): SemanticPlugin = app.plugin[SemanticPlugin] match {
    case Some(plugin) => plugin
    case _            => throw new PlayException("Bigdata plugin Error", "BigdataPlugin has not been initialized! Please edit your conf/play.plugins file and add the following line: '10000:org.denigma.semantic.BigDataPlugin' (1000 is an arbitrary priority and may be changed to match your needs).")
  }


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


}