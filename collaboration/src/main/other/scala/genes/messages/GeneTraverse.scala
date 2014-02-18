package org.denigma.genes.messages

import scalax.collection.GraphTraversal._
import org.denigma.genes.models.FishEye

/**
 * Insturction to make a graph traversal starting from gene with name
 * @param name Name of a gene
 * @param depth depth of traversal, 1 by default
 * @param direction direction of traversal
 * @param strict defines if we find the exact match or any word inclouding our name
 * @param searchAlias defines if we search in aliases or only use gene names
 */
case class GeneTraverse(name:String,depth:Int = 1, direction:Direction = AnyConnected, strict:Boolean = false, searchAlias:Boolean = true)

/**
 * Companion object of GeneTraverse
 */
object GeneTraverse {
  def fromFishEye(fy:FishEye) = new GeneTraverse(fy.name,fy.depth,GeneTraverse.str2direction(fy.direction),false,true)

  def str2direction(str:String) = str match
  {
    case "AnyConnected" =>AnyConnected
    case "Predecessors"=>Predecessors
    case "Successors" =>Successors
    case _ =>AnyConnected
  }


  def direction2str(dir:Direction) = dir match
  {
    case AnyConnected=>"AnyConnected"
    case Predecessors=>"Predecessors"
    case Successors =>"Successors"
  }
}
