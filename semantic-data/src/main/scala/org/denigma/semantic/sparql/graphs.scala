package org.denigma.semantic.sparql




/**
 * grph that may contains only triplets (used by INSERT/DELETE DATA)
 * @param id id of the Graph
 * @param triplets list of triplets to be added
 */
class TripletGraph(val id:IRIPatEl)(val triplets:List[Trip]) extends SPARQLGraph{

  type Triplet = Trip

}

class PatternGraph(val id:IRIPatEl)(val triplets:List[TripletPattern]) extends SPARQLGraph
{

  type Triplet = TripletPattern

  override def children: List[GroupElement] = triplets.toList
}

trait SPARQLGraph extends GP{

  def id:IRIPatEl

  type Triplet<:GroupElement

  val triplets:List[Triplet]

  override def stringValue: String = id match {
    case v:VarExtended=> s" GRAPH <${v.variable.name}> \n{ ${this.foldChildren} } "
    case other=> s" GRAPH ${other.stringValue} \n{ ${this.foldChildren} } "
  }



  override def children: List[GroupElement] = triplets.toList

}
