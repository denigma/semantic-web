package org.denigma.genes.workers


import org.denigma.genes.models._
import org.denigma.actors.staff.EventActor
import org.denigma.actors.messages.{Push, ActorEvent}
import akka.actor.ActorSelection
import org.denigma.genes.settings.GeneSettings


/**
 * This actors intersects with genes database
 * It receives commands mostly in a form of events published by memberactors
 */
class GeneDataWorker extends EventActor with GeneDataCenter with GeneConfig {

  override def home: String = System.getProperty("user.home")
  override def repo: String = ".aduna/openrdf-sesame/repositories"
  override def dbName = "genes"

  val settings = GeneSettings(context.system)


  override def preStart() = {
    super.preStart()
    this.load()
  }

  /**
   * Some operations with genes to be handled by the actor
   * @return
   */
  def genesOperations:PartialFunction[Any,Unit] = {
    case ActorEvent("genesgraph",fy:FishEye,actor) => actor ! Push(this.now,this.traverseFishEye(fy))


  }

  def suggestOperations:PartialFunction[Any,Unit] = {
    case ActorEvent("genes",Suggest(str,field),actor)=>
      val mess = Push(now,Suggestion(this.lookup.suggestKeyList(str),"genes"))
      actor ! mess
  }

  /**
   * receive method that combines search and graph operations
   * @return
   */
  def receive= this.suggestOperations orElse this.genesOperations



}