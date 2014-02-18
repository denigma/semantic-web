package org.denigma.semantic.graphs

import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._
import scalax.collection.edge._
import org.openrdf.model.impl.URIImpl

//
//object SemanticGraph{
//
//
//  final implicit class EdgeAssoc[N1](val n1: N1) extends AnyVal {
//    @inline def -->[N >: N1, N2 <: N](n2: N2) = new   DiEdge[N](Tuple2(n1, n2))
//  }
//}
//import SemanticGraph._
//
//// labeled directed edge
//import scalax.collection.edge.Implicits._ // shortcuts
//
//object PlayWithGraphs {
//
//  val e = new URIImpl("1") --> new URIImpl("1")
//  val g: Graph[URIImpl, DiEdge] = Graph(e)
//}