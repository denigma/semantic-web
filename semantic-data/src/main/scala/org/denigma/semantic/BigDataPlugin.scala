package org.denigma.semantic

import play.api.Plugin
import org.topbraid.spin.system.SPINModuleRegistry

/**
 * Neo4j plugin to stop server.
 * @author : bsimard
 */
class BigDataPlugin(app: play.api.Application) extends Plugin {

  implicit val lg = play.api.Logger.logger

  override def onStart{
    lg.debug("Starting  BigData plugin")
    SG.cleanIfInTest()
    SPINModuleRegistry.get.init()
    SG.db = new SG()
    SG.start()
  }

  override def onStop(){
    lg.debug("Stopping BigData plugin")
    SG.db.repo.shutDown()

  }
}
