package org.denigma.genes.settings

import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import scala.concurrent.duration.Duration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit


/**
 * Stores settings inside
 */
object GeneSettings extends ExtensionId[GeneSettingsImpl] with ExtensionIdProvider {

  override def lookup = GeneSettings

  override def createExtension(system: ExtendedActorSystem) =
    new GeneSettingsImpl(system.settings.config)
}