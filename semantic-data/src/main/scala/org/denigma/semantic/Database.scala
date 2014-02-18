package org.denigma.semantic

import akka.actor._
import org.denigma.semantic.actors.NamedActor
import java.util.Properties
import java.io._
import org.denigma.semantic.data.SemanticStore
import java.util
import org.openrdf.repository.RepositoryResult
import org.denigma.semantic.quering.QueryWizard

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.model._
import scala.collection.immutable._
import scala.collection.JavaConversions._



/*
database extension that is reachable from all actors
 */
object Database extends ExtensionId[DatabaseImpl]
  with ExtensionIdProvider {
  //The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup() = Database

  //This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system:ExtendedActorSystem) = new DatabaseImpl(system)

}

/*
extension that provides nice db access
 */
class DatabaseImpl(val sys: ExtendedActorSystem)  extends Extension{

  val lg = sys.log

  def config = sys.settings.config



  def inTest = Play.isTest
  def inDev = Play.isDev
  def inProd = Play.isProd



  def inTest(action: =>Unit):Unit = if(this.inTest) { action}

  lg.debug("HELLO DATABASE EXTENSION")
  lg.info(s"akka system is inTest == ${inTest}")







}

trait SemanticActor extends NamedActor{

  def db: DatabaseImpl = Database(context.system)


}