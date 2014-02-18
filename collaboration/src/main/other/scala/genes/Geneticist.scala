package org.denigma.genes

import org.denigma.actors._
import org.denigma.genes.managers.{SearchManager, GeneticManager}
import play.api.libs.json.JsValue
import java.util.Date
import org.denigma.video.VideoMember

/**
 * This actors inherits from member actors and is used for communications with the user
 * in particular Geneticist extends some useful traits that let him
 * process genetic and search info
 */
class Geneticist extends VideoMember with GeneticManager with SearchManager
{


  override def preStart() = {
    log.debug(s"Starting Geneticist actor with name = ${self.path.name}")
    this.checkConnectionTimeout()

  }

  /**
   * Geneticist actor receive functions, gene and search related behaviour is added here
   * @return
   */
  override def receive =  this.receiveGene orElse this.receiveSearch orElse super.receive

  override def parseOperations: this.ChannelRequestRoomContentDateParser= this.parseSearch orElse this.parseGenes orElse super.parseOperations
}