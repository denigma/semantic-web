package org.denigma.genes.mock

import akka.actor.ActorRef
import akka.testkit.TestActorRef
import scala.reflect.ClassTag
import org.denigma.genes.{GeneMain, Geneticist}
import org.denigma.actors.messages.ActorEvent
import org.denigma.actors.rooms.{ChatRoomActor, RoomActor}


/**
 * Class to test mock master actor for genes
 */
class MockGeneMaster extends MockGeneMain[Geneticist]



class MockGeneMain[T<:Geneticist:ClassTag]  extends GeneMain[T] {

  var testMembers = Map.empty[String,TestActorRef[T]]

  var geneDataWorkerActor:MockGeneDataWorker = null

  override def preStart() = {
    log.debug(s"$name started")
    this.initAuth()
    this.addRoom[ChatRoomActor]("all")
    this.defaultRoom = this.rooms.head
    val gName = "geneDataWorker"
    this.createTestActor[MockGeneDataWorker](gName)(context.system) match
    {
      case ref =>
        this.geneDataWorker = ref
        this.geneDataWorkerActor = ref.underlyingActor
        this.makeSubscriptions()
    }
  }



  /**
   * creates TestActorRefs instead of real once
   * @param username name of the user that has connected
   * @return
   */
  override def addMember(username:String):ActorRef =
  {
    val user = this.createTestActor[T](username)(context.system)
    members+=username->user
    testMembers+=username->user
    this.onAddMember(user)
    user
  }

  def onDebug(actor:ActorRef) = this.subscribe(actor,"debug")

  override def addRoom[T<:RoomActor:ClassTag](roomName:String): ActorRef ={
    this.addTestRoom[T](roomName)
  }



}
