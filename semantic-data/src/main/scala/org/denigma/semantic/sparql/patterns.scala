package org.denigma.semantic.sparql

import org.denigma.semantic.model.{QueryElement, BlankNode, IRI}
import org.openrdf.model.{Statement, Value, Resource, URI}



/**
 * Basic graph pattern
 * @param s Subject (Var,Literal or Resource)
 * @param p Predicate (Var,Literal or Resource)
 * @param o Object (Var,Literal or Resource)
 * @param c Context (Var,Literal or Resource or Null)
 */
case class Pat(s:ResourcePatEl,p:IRIPatEl,o:ValuePatEl,c:IRIPatEl= null) extends QuadPattern



trait QuadPattern extends TripletPattern{
  def hasContext = c!=null
  def c:ResourcePatEl
  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")

  override def canBind(st:Statement) = (!this.hasContext || c.canBind(st.getContext)) && super.canBind(st)

  def contextOrNull = if(this.hasContext) this.c.resourceOrNull else null

}

trait TripletPattern extends QueryElement
{
  def s:ResourcePatEl
  def p:IRIPatEl
  def o:ValuePatEl
  override def stringValue: String = s"\n <${s.stringValue} ${p.stringValue} ${o.stringValue} .\n"


  /**
   * Check if the statement fits the pattern
   * @param st
   * @return
   */
  def canBind(st:Statement) = s.canBind(st.getSubject) && p.canBind(st.getPredicate) && o.canBind(st.getObject)
}

trait IRIPatEl extends ResourcePatEl
{
  def IRIorNull:URI
}
trait ResourcePatEl extends ValuePatEl
{
  def resourceOrNull:Resource
}
trait ValuePatEl extends PatternElement
{
  def canBind(value:Value):Boolean
  def valueOrNull:Value
}
/*
 TODO: add binding for vars
 */
trait PatternElement extends QueryElement
{
  def isVar:Boolean = false
  def isIRI:Boolean = false
  def isLiteral = false
  def isBlankNode = false
}