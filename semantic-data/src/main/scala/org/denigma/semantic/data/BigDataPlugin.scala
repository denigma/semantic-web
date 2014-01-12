package org.denigma.semantic.data

import play.api.{Logger, Plugin}
import play.Play

/**
 * Neo4j plugin to stop server.
 * @author : bsimard
 */
class BigDataPlugin(app: play.api.Application) extends Plugin {

  implicit val lg = play.api.Logger.logger

  override def onStart{
    lg.debug("Starting  BigData plugin")
    SG.cleanIfInTest()
    SG.db = new SG()
  }

  override def onStop(){
    lg.debug("Stopping BigData plugin")
    SG.db.repo.shutDown()

  }
}
