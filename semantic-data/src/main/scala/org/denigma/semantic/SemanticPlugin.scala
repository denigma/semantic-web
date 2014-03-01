package org.denigma.semantic

import play.api.Plugin
import org.denigma.semantic.platform.SP

/**
Play 2 Plugin that runs bigdata database and other semanticdb stuff
@param app play application
 */
class SemanticPlugin(app: play.api.Application) extends Plugin
{

  implicit val lg = play.api.Logger.logger

  /*
  Fires when the Play2 app to which this plugin is attached starts
   */
  override def onStart(){
    lg.debug("Starting  BigData plugin")

    SP.extractConfig(app) //gets PlayConfig file and extracts info from it
    SP.cleanIfInTest() //cleans some files if run in test mode
    SP.start(app) //starts everything (incl. database)
  }

  override def onStop(){
    lg.debug("Stopping BigData plugin")
    SP.stop()

  }
}

