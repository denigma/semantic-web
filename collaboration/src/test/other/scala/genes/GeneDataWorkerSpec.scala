package org.denigma.genes

import play.api.libs.iteratee._

import scala.concurrent.duration._

import org.scalatest._


import akka.testkit._

import akka.actor._
import play.api.libs.json._
import  scala.language.postfixOps
import org.denigma.genes.mock.MockGeneMaster
import org.denigma.genes.models.{Gene, FishEye}
import org.denigma.actors.messages.{Push, ActorEvent}
import org.denigma.genes.messages.{GraphChange, GeneTraverse}
import scalax.collection.GraphTraversal.Successors
import scalax.collection.edge.LkDiEdge
import org.denigma.actors.base.MainSpec
import org.denigma.video.mock.MockVideoMaster
import org.denigma.actors.models.Suggest


/**
 * Spec to test how gene data worker works
 */
class GeneDataWorkerSpec(_system: ActorSystem)  extends MainSpec[MockGeneMaster](_system)// extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{

def this() = this(ActorSystem("MySpec"))
val geneDataWorker = masterActor.geneDataWorker
val geneDataWorkerActor = masterActor.geneDataWorkerActor

"Genes dataworker" should {

  val pref = "http://denigma.de"
  val noname = s"$pref/nonamegene"
  val name =  s"$pref/gene50"
  val fromName =  s"$pref/gene5"
  val toName =  s"$pref/gene500"

  "give gene names suggestions" in
  {
    val n = "50"
    val res = this.geneDataWorkerActor.lookup.suggestKeyList(n)
    res.head.contains(name).shouldEqual(true)
  }

  "respond to suggest messages" in {
    val probe = new TestProbe(this._system)
    this.masterActor.onDebug(probe.ref)
    this.masterActor.publish(ActorEvent("genes",Suggest("50","genes"),probe.ref))
    probe.expectMsgPF(200 milliseconds){
        case answer @ Push(now,Suggestion(list:List[String],"genes"))=>list.head.contains(name).shouldEqual(true)
        case answer=>
          val a = answer
          this.fail("no message"); null
    }

  }
  /*
  "answer to fisheye events" in
  {

    //TODO: fix the bug with answers

    val probe = new TestProbe(this._system)
    this.masterActor.onDebug(probe.ref)


    val gw = this.masterActor.geneDataWorkerActor
    val fy = new FishEye("genesgraph","50",GeneTraverse.direction2str(Successors),1,"load")

    this.masterActor.publish(ActorEvent("genesgraph",fy,probe.ref))

    probe.expectMsgPF(200 millis){

      case Push(date,gu:GraphChange[Gene, LkDiEdge])=>
        val list = gu.graph.nodes.toList
        list.contains(Gene(name)).shouldEqual(true)

        list.contains(Gene(s"$pref/gene501")).shouldEqual(true)
        list.contains(Gene(s"$pref/gene51")).shouldEqual(false)
        list.contains(Gene(s"$pref/gene601")).shouldEqual(false)
        list.contains(Gene(noname)).shouldEqual(false)
        list.contains(Gene(s"$pref/gene508")).shouldEqual(true)

        case some =>
          val v = some
          this.fail("some weird message received")
    }

  }


*/
}


}
