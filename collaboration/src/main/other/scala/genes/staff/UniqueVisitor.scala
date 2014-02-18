package org.denigma.genes.staff

import scalax.collection.mutable.Graph
import scalax.collection.edge.LkDiEdge
import scalax.collection.GraphTraversal.VisitorReturn._


trait UniqueVisitor[TN,TE] extends UniqueNodeVisitor[TN]
{
  def add(edge:TE):Unit
}

class UniqueNodeVisitor[TN]
{
  var nodes = Set.empty[TN]

  def add(node:TN):VisitorReturn = {
    nodes = nodes + node
    Continue
  }

  //def add(edge:TE):Unit = {}

  def nodeFilter(node:TN) = !this.nodes.contains(node)

}