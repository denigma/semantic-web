package org.denigma.genes.settings
import akka.actor.Extension
import com.typesafe.config.Config
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import scala.concurrent.duration.Duration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit
import org.denigma.data.SesameConfig

class GeneSettingsImpl(config: Config)  extends Extension {

  lazy val home: String = config.getString("genes.db.home")
  lazy val repo: String = config.getString("genes.db.repo")
  lazy val name: String = config.getString("genes.db.name")
}