package org.denigma.genes


import scala.concurrent.duration._


import akka.testkit._

import akka.actor._
import org.denigma.actors.messages._
import play.api.libs.json._
import  scala.language.postfixOps
import org.denigma.genes.models._
import org.denigma.genes.mock.MockGeneMaster


import scala.Predef._
import scalax.collection.mutable.{Graph => MGraph}
import scalax.collection.edge._

import scalax.collection.mutable._
import scalax.collection.edge.Implicits._
import org.denigma.actors.messages.Push
import scala.Some
import org.denigma.actors.messages.Received
import org.denigma.genes.models.GeneInteraction
import org.denigma.genes.models.Gene
import org.denigma.genes.messages.{GraphChange, GeneTraverse}
import scalax.collection.GraphTraversal.Successors
import org.denigma.genes.managers.GeneticManager
import org.denigma.actors.base.MainSpec
import org.denigma.genes.settings.GeneSettings
import org.denigma.actors.models.Suggestion


/**
 * test spec to test master actor
 * */
class GeneticistSpec(_system: ActorSystem) extends MainSpec[MockGeneMaster](_system) //extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{

  def this() = this(ActorSystem("MySpec"))

  "Geneticist" should {

    val pref = "http://denigma.de"
    val noname = s"$pref/nonamegene"
    val name =  s"$pref/gene50"
    val fromName =  s"$pref/gene5"
    val toName =  s"$pref/gene500"


    this.addUser(user1,password1)
    this.addUser(user2,password2)
    this.addUser(user3,password3)

    val(Some(u1),Some(u2),Some(u3)) =
      (masterActor.testMembers.get(user1),masterActor.testMembers.get(user2),masterActor.testMembers.get(user3))
    val (ua1,ua2,ua3) = (u1.underlyingActor,u2.underlyingActor,u3.underlyingActor)

    val d= ua1.now


    "send graph data" in {

      val graph = Graph.empty[Gene,LkDiEdge]
      var genes = List.empty[Gene]
      for(i <- 0 until 10)
      {
        val sx= "X"+i.toString
        val sy= "Y"+i.toString

        val gx = Gene(sx)
        val gy = Gene(sy)
        val e = (gx~+#>gy)(GeneInteraction(sx+"_2_"+sy))
        graph.add(e)
      }
      val info: JsValue = ua1.pack2Request("genesgraph",graph,"push")
      val str = Json.stringify(info)
      str.contains("X0").shouldEqual(true)
      str.contains("Y7").shouldEqual(true)
      str.contains("X11").shouldEqual(false)
      str.contains("Y1A").shouldEqual(false)
      str.contains("X3_2_Y3").shouldEqual(true)
      str.contains("X6_2_Y6").shouldEqual(true)
      str.contains("X6_2_Y3").shouldEqual(false)
      str.contains("X3_2_Y6").shouldEqual(false)

      val e = str

    }




    "send debug messages" in {
      val probe = new TestProbe(system)
      this.masterActor.onDebug(probe.ref)
      val dw = "debug works!"
      this.masterActor.geneDataWorkerActor.debug(dw)
      probe.expectMsg(200 millis,ActorEvent("debug",dw,this.masterActor.geneDataWorker))


    }


    val lookContent = Json.obj(
      "query" -> "50",
      "field" -> "genes")

    val look:JsValue = Json.obj(
      "channel"->"genes",
      "content" -> lookContent,
      "request"->"lookup",
      "room"->"all"
    )

    "parse lookup content" in {


      val probe = new TestProbe(system)
      this.masterActor.onDebug(probe.ref)

      //u1 ! Received(this.masterActor.now, look)
      ua1.parseSuggest("lookup",lookContent)(masterActor.now)
      //ua1.publish(ActorEvent("genes",Suggest("50","genes"),u1))

      probe.expectMsgPF(200 millis){
        case ActorEvent("debug",Push(now,Suggestion(value:List[String],"genes")),ref:ActorRef) =>
          value.head.contains(name).shouldEqual(true)

        case some =>
          this.fail("some weird message received")
      }

    }

    "answer lookup requests" in {


      val probe = new TestProbe(system)
      this.masterActor.onDebug(probe.ref)
      val n = this.masterActor.now
      //u1 ! Received(n, look)
      //ChannelRequestRoomContentDateParser
      ua1.receive(Received(n, look))
      //ua1.parseSearch("genes","lookup","all",lookContent,masterActor.now)

      //ua1.parse(look)(this.masterActor.now)
      //ua1.parseSuggest("lookup",lookContent)(masterActor.now)
      //ua1.publish(ActorEvent("genes",Suggest("50","genes"),u1))
      probe.expectMsgPF(300 millis){
        case ActorEvent("debug",Push(now,Suggestion(value:List[String],"genes")),ref:ActorRef) =>
          value.head.contains(name).shouldEqual(true)

        case some =>
          this.fail("some weird message received")
      }

    }

    val fyContent = Json.obj(
      "graph" -> "genesgraph",
      "name" -> "50",
      "direction" -> "Successors",
      "depth" -> 1,
      "mode" -> "load")

    val fy:JsValue = Json.obj(
      "channel"->"genes",
      "content" -> fyContent,
      "request"->"lookup",
      "room"->"all"
    )

    "parse graph well" in
     {

       val gw = this.masterActor.geneDataWorkerActor
       val fy = new FishEye("genesgraph","50",GeneTraverse.direction2str(Successors),1,"load")
       val g: Graph[Gene, LkDiEdge] = gw.traverseFishEye(fy).graph
       g.contains(Gene(s"$pref/gene501")).shouldEqual(true)
       g.contains(Gene(s"$pref/gene601")).shouldEqual(false)

       val gm = ua1.asInstanceOf[GeneticManager] //just to see less methods

       val req = gm.pack2Request(GraphChange("genesgraph",g,"save"))
       val cont = req \ "content"
       //val conts = Json.stringify(cont)
       val gp = gm.parseGraph(cont)


       gp.nodes.size.shouldEqual(g.nodes.size)
       gp.edges.size.shouldEqual(g.edges.size)
       gp.contains(Gene(s"$pref/gene501")).shouldEqual(true)
       gp.contains(Gene(s"$pref/gene601")).shouldEqual(false)


       val list = gp.nodes.toList
       list.contains(Gene(name)).shouldEqual(true)

       list.contains(Gene(s"$pref/gene501")).shouldEqual(true)
       list.contains(Gene(s"$pref/gene51")).shouldEqual(false)
       list.contains(Gene(s"$pref/gene601")).shouldEqual(false)
       list.contains(Gene(noname)).shouldEqual(false)
       list.contains(Gene(s"$pref/gene508")).shouldEqual(true)

     }

    "receive and answer fisheye events" in
    {

      val probe = new TestProbe(this._system)
      this.masterActor.onDebug(probe.ref)


      val gw = this.masterActor.geneDataWorkerActor
      val fy = new FishEye("genesgraph","50",GeneTraverse.direction2str(Successors),1,"load")

      this.masterActor.publish(ActorEvent("genesgraph",fy,probe.ref))

      probe.expectMsgPF(200 millis){

        case Push(date,gu:GraphChange[Gene, LkDiEdge]) =>
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

    "get settings with repositories" in {

      /*
      home = "~/home"
      repo = ".aduna/openrdf-sesame/repositories"
      name = "genes"
      */

      val set = GeneSettings(_system)
      set.home.shouldEqual("~/home")
      set.repo.shouldEqual(".aduna/openrdf-sesame/repositories")
      set.name.shouldEqual("genes")
    }
  }

}
