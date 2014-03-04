package org.denigma.semantic.sparql

import org.denigma.semantic.model.IRI


/**
 * Basic graph pattern
 * @param s Subject (Var,Literal or Resource)
 * @param p Predicate (Var,Literal or Resource)
 * @param o Object (Var,Literal or Resource)
 * @param c Context (Var,Literal or Resource or Null)
 */
case class Pat(s:PatternElement,p:PatternElement,o:PatternElement,c:PatternElement= null) extends QuadPattern
case class Trip(s:PatternElement,p:PatternElement,o:PatternElement) extends TripletPattern

trait TripletPattern extends GroupElement
{
  def s:PatternElement
  def p:PatternElement
  def o:PatternElement
  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString} .\n"
}

trait QuadPattern extends TripletPattern{
  def hasContext = c!=null
  def c:PatternElement
  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")
}

object GRAPH {

  def apply(graphId:IRI)(elements:TripletPattern*) = new Graph(graphId)(elements:_*)
}

class Graph(id:PatternElement)(val triplets:TripletPattern*) extends GP
{
  override def stringValue: String = s" GRAPH \n{ ${id.stringValue} ${this.foldChildren} } "

  override def children: List[GroupElement] = triplets.toList
}