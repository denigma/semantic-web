package org.denigma.genes.console


import org.denigma.genes.workers.GeneDataCenter
import scalax.collection.io.json.descriptor.NodeDescriptor
import org.denigma.genes.models._
import scala.Predef._
import scalax.collection.mutable.{Graph => MGraph}
import scalax.collection.edge._

import scalax.collection.edge.Implicits._

import scalax.collection.io.json.descriptor.predefined._
import scalax.collection.io.json._
import scalax.collection.io.json.exp._

/**
 * Console application to be used to profile some memory or CPU instensive stuff
 */
object GeneDataProfiler extends App with GeneDataCenter
{



  /**
   * Node descriptor is used in parsing of nodes content to and from JSON
   */
  val nodeDesc:NodeDescriptor[Gene] = new NodeDescriptor[Gene](typeId = "Genes") {
    def id(node: Any) = node match {
      case Gene(name) => name
    }
  }

  val desc = new Descriptor[Gene](nodeDesc, LkDi.descriptor(GeneInteraction("")))



  val e1 = (Gene("http://www.ncbi.nlm.nih.gov/gene/12929")~+#>Gene("http://www.ncbi.nlm.nih.gov/gene/18477"))(GeneInteraction("http://denigma.de/data/entry/interactsWith"))
  val e2 = (Gene("http://www.ncbi.nlm.nih.gov/gene/53975")~+#>Gene("http://www.ncbi.nlm.nih.gov/gene/27049"))(GeneInteraction("http://denigma.de/data/entry/interactsWith"))

  //load()
  this.graph.add(e1)
  this.graph.add(e2)

  //this.graph.foreach(u=>println(u.toString))




  val export = new Export[Gene,LkDiEdge](graph, desc)

  import export._
  val (nodesToExport, edgesToExport) = (jsonASTNodes, jsonASTEdges)
  val astToExport = jsonAST(nodesToExport ++ edgesToExport)
  //println(jsonText(jsonAST(edgesToExport)))
  println(jsonText(astToExport))

  println("")
  println("export finished")
  println("============================================")




}
