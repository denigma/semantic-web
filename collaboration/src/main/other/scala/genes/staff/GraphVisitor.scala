package org.denigma.genes.staff

import scalax.collection.mutable._
import scalax.collection.mutable.Graph._
import scalax.collection.GraphTraversal.VisitorReturn._
import scalax.collection.GraphPredef
import GraphPredef.EdgeLikeIn

/**
 * This class is needed for genes graph traversals
 * @param edgeManifest
 * @param config
 * @tparam N Nodes type
 * @tparam E Edges type
 */
class GraphVisitor[N,E[X] <: EdgeLikeIn[X]](implicit edgeManifest: Manifest[E[N]], config: Config = defaultConfig)
{
  val graph = Graph.empty

  def addNode[T](node:T):VisitorReturn = node match {
    case node:this.graph.type#NodeT=>
      val nd = node.value
      this.graph.add(nd)
      Continue


    case other=>
      println("cannot recognized the node being traversed")
      Continue
  }

  def addEdge[T](edge:T):Unit = edge match {
    case edge:this.graph.type#EdgeT =>
      //workaround for type problems
      val ee = edge.toEdgeIn
      this.graph.add(ee)


    case other=>
      println("cannot recognized the edge being traversed")
  }

  /*
  def addNode(node:N) = {this.graph.add(node.value); Continue}

  def addEdge(edge:E[N]):Unit = this.graph.add(edge)
  */

}
