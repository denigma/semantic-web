package org.denigma.genes
import scala.reflect.ClassTag
import akka.actor.ActorRef
import org.denigma.video.VideoMain
import org.denigma.genes.workers.GeneDataWorker
import org.denigma.actors.rooms.{ChatRoomActor, RoomActor}

//TODO: make something with inheritance because it is weird that gene actors inherit video ones
/**
 * Main actor that works with genetics
 * this class use members that inherit from Geneticist class and are capable of processing some graph data
 *
 *
 */
class GeneMain[T<:Geneticist:ClassTag] extends  VideoMain[T]
{

  /**
   * This actors do all work related to genes
   */
  var geneDataWorker:ActorRef = null

  /**
   * Here we initialize the actors and make subscriptions
  */
  override def preStart() =
  {
    log.debug(s"$name started")
    this.initAuth()
    this.addRoom[ChatRoomActor]("all")
    this.defaultRoom = this.rooms.head
    this.geneDataWorker= this.createActor[GeneDataWorker]("geneDataWorker")
    this.makeSubscriptions()
  }

  /**
   * Subscribes actors to events
   * in particulary to cache and geneDataWorker actors, so they will be accessible through events from anywhere
   * @return
   */
  def makeSubscriptions() = {
    this.subscribe(this.geneDataWorker,"genes")
    this.subscribe(this.geneDataWorker,"genesgraph")

  }

}
