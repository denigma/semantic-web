package org.denigma.genes.mock

import org.denigma.actors.staff.NamedActor
import org.denigma.genes.workers.GeneDataWorker
import org.denigma.actors.messages.{Push, ActorEvent}
import akka.actor.ActorRef
import org.denigma.actors.models.Suggestion


class MockGeneDataWorker extends GeneDataWorker with MockGeneDataCenter{

  override def suggestOperations:PartialFunction[Any,Unit] = {
    case ActorEvent("genes",Suggest(str,field),actor)=>
      val mess = Push(now,Suggestion(this.lookup.suggestKeyList(str),"genes"))
      actor ! mess
      this.debug(mess)
  }

  def debug[T](value:T){
    this.publish(ActorEvent("debug",value,self))
  }

}
