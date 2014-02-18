package org.denigma.genes.messages

import scalax.collection.mutable._
import scalax.collection.mutable.Graph._
import scalax.collection.GraphTraversal.VisitorReturn._
import scalax.collection.GraphPredef._
import scalax.collection.GraphPredef
import GraphPredef.EdgeLikeIn
import org.denigma.actors.messages.{EventLike, ActorEvent}

/**
 * Tells about the graph change
 */
case class GraphChange[N,E[X] <: EdgeLikeIn[X]](name:String,graph:Graph[N,E],mode:String ="push") extends EventLike
