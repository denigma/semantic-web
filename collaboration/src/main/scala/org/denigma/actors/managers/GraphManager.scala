package org.denigma.actors.managers

import org.denigma.actors.staff.ChatActorLike


/**
 * This trait will be responsible for genes graphs
 */
abstract trait GraphManager[N,E[X] <: scalax.collection.GraphPredef.EdgeLikeIn[X]] extends ChatActorLike
{
 // val graph:Graph[N,E] = Graph.empty[N,E]




}
