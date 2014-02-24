package org.denigma.semantic.platform

import play.api.{PlayException, Configuration}
import org.denigma.semantic.storage.DBConfig
import org.denigma.semantic.SemanticPlugin

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

}