package org.denigma.genes.workers

import org.denigma.data.SesameConfig
import akka.actor.Actor
import org.denigma.genes.settings.GeneSettings

/**
 * Loads genes from traits
 */
trait GeneConfig extends SesameConfig{
  self: Actor ⇒
  override def home: String = GeneSettings(context.system).home
  override def repo: String = GeneSettings(context.system).repo
  override def dbName = GeneSettings(context.system).name

}
