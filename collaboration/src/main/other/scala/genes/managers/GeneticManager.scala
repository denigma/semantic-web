package org.denigma.genes.managers


import org.denigma.genes.models._
import org.denigma.actors.managers.GraphManager
import org.denigma.actors.models.RequestInfo
import play.api.libs.json.{JsError, JsSuccess, Json, JsValue}
import scalax.collection.io.json.descriptor.{Descriptor, NodeDescriptor}
import scalax.collection.io.json.descriptor.predefined.LkDi
import org.denigma.actors.messages.{ActorEvent, Push}
import org.denigma.genes.messages._
import java.util.Date

import scala.Predef._
import scalax.collection.mutable.{Graph => MGraph}
import scalax.collection.edge._

import scalax.collection.mutable._
import scalax.collection.io.json.exp.Export
import scalax.collection.io.json.JsonGraphCoreCompanion

/**
 * This trait let the Member actors to work with genes search and graph data
 */
trait GeneticManager extends GraphManager[Gene,LkDiEdge] with FishEyeFormatter
{
  /**
   * Here we create a directed labeled graph for genes
   */
  val graph:Graph[Gene,LkDiEdge] = Graph.empty[Gene,LkDiEdge]


  /**
   * Node descriptor is used in parsing of nodes content to and from JSON
   */
  val nodeDesc:NodeDescriptor[Gene] = new NodeDescriptor[Gene](typeId = "Genes") {
    def id(node: Any) = node match {
      case Gene(name) => name
    }
  }

  val desc:Descriptor[Gene] = new Descriptor[Gene](nodeDesc, LkDi.descriptor(GeneInteraction("")))


  /**
   * Adds to member support for some operations with genes
   * @return
   */
  def receiveGene:  this.Receive = {

    case Push(date, value:Graph[Gene,LkDiEdge]) => this.sendJson2Client(pack2Request("genesgraph",value,"push"))(date)

    case Push(date, value:GraphChange[Gene,LkDiEdge]) => this.sendJson2Client(pack2Request(value.name,value.graph,value.mode))(date)

    case Push(date,value:FishEye) =>this.sendJson2Client(this.pack2Request(value,"push"))(date)


  }


  /**
   * Sends fisheye command to the client
   * It does not sed a data but only and instructions for clientside traversal
   * @param fy FishEye
   * @param request type of request
   * @return JSON of FishEye
   */
  def pack2Request(fy: FishEye, request:String):JsValue = Json.toJson(this.pack2RequestInfo(fy,request))

  def pack2RequestInfo(fy:FishEye,request:String): RequestInfo =   {
    val content: JsValue =  Json.toJson(fy)(this.writeFishEye)
    RequestInfo(fy.name,content,request)
  }

  /**
   * Packs to JSON request graph message
   * @param value graph changes that will be send to the client
   * @return
   */
  def pack2Request( value:GraphChange[Gene,LkDiEdge]):JsValue = pack2Request(value.name,value.graph,value.mode)


  /**
   * Packes graph to request
   * @param channel channel where I pack the graph
   * @param g graph that is packed
   * @param request request with which it is packed
   * @return resulting json value
   */
  def pack2Request(channel:String,g: Graph[Gene,LkDiEdge], request:String):JsValue =
    Json.toJson(pack2RequestInfo(channel,g,request))(writeRequestInfo)

  /**
   * Packs genes graph to a request info
   * @param channel channel which is used for the graph
   * @param g Genes graph
   * @param request request, if load it reloads the graph, if update it updates
   * @return JSON of the genes graph
   */
  def pack2RequestInfo(channel:String,g: Graph[Gene,LkDiEdge],request:String): RequestInfo = {

    //another way to export
    //val expLGraph= g.toJson(quickJson)    //here IDEA cannot find implicit toJson method so I have to use another way to export
    val export = new Export[Gene,LkDiEdge](g, desc)

    val (nodesToExport, edgesToExport) = (export.jsonASTNodes, export.jsonASTEdges)
    val astToExport = export.jsonAST(nodesToExport ++ edgesToExport)

    val txt: String = export.jsonText(astToExport)
    RequestInfo(channel,Json.parse(txt),request)


  }

  /**
   * Parses graph from JSON
   * @param content JSON to be parsed
   * @return GenesGraph
   */
  def parseGraph(content:JsValue): Graph[Gene, LkDiEdge] = {
    val jgComp = new JsonGraphCoreCompanion[Graph](Graph)
    log.debug(s"${name}: Graph received and parsed")
    jgComp.fromJson[Gene,LkDiEdge](Json.stringify(content),desc)
  }

  /**
   * Parses genes graph-related data (graph content or traversal instructions)
   * @return
   */
  def parseGenes:this.ChannelRequestRoomContentDateParser= {

    case ("genesgraph","fisheye",room,content,date) =>
      content.validate[FishEye](readFishEye) match
      {
        case result:JsSuccess[FishEye] =>
          this.publish(ActorEvent("genesgraph",result.value,self))
          log.debug(s"${name}: FishEye with content = ${content.toString()}  received at ${date.toString} and ActorEvent for it created")


        case result:JsError=>
          log.error(s"${name}: error in parsing FishEye json: unable to parseOperations ${content.toString()} at ${date.toString} ")

      }

    case ("genesgraph",req,room,content,date) =>
      this.publish(GraphChange("genesgraph",this.parseGraph(content),req))



  }
}
