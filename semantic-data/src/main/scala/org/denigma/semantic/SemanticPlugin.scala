package org.denigma.semantic

import play.api.Plugin
import org.denigma.semantic.platform.WithSemanticPlatform

/*
Plugin that runs bigdata database and other semanticdb stuff
 */
class SemanticPlugin(app: play.api.Application) extends Plugin with WithSemanticPlatform{

  implicit val lg = play.api.Logger.logger

  override def onStart(){
    lg.debug("Starting  BigData plugin")

    sp.extractConfig(app)
    sp.cleanIfInTest()
    //SPINModuleRegistry.get.init()
    sp.start(app, play.api.Logger.logger)
  }

  override def onStop(){
    lg.debug("Stopping BigData plugin")
    sp.stop()

  }
}

