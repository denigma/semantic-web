package org.denigma.semantic.platform
import play.api.Play.current
import org.denigma.semantic.commons.AppLogger
import play.api.{Application, Configuration, Play}


trait AppConfig {
  implicit val lg = new AppLogger(play.api.Logger.logger)

  def inTest: Boolean = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd

  //TODO: rename
  def currentConfig(conf:Configuration)(implicit app: Application) = if(Play.isTest(app))  conf.getConfig("test").get else if(Play.isDev(app)) conf.getConfig("dev").get   else conf.getConfig("prod").get

  lazy val appConfig: Configuration = Play.current.configuration
  lazy val currentAppConfig = this.currentConfig(appConfig)(Play.current)
}
