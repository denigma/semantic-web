package org.denigma.semantic.sparql

import org.denigma.semantic.model.{ BlankNode, IRI}
import org.openrdf.model.{Value, Resource, URI}
import com.hp.hpl.jena.rdf.model._
import com.bigdata.rdf.internal.impl.literal.FullyInlineTypedLiteralIV



/**
 * Basic graph pattern
 * @param s Subject (Var,Literal or Resource)
 * @param p Predicate (Var,Literal or Resource)
 * @param o Object (Var,Literal or Resource)
 * @param c Context (Var,Literal or Resource or Null)
 */
case class Pat(s:ResourcePatEl,p:IRIPatEl,o:ValuePatEl,c:IRIPatEl= null) extends QuadPattern with SesameSearcher



trait TripletPattern extends GroupElement
{
  def s:ResourcePatEl
  def p:IRIPatEl
  def o:ValuePatEl
  override def stringValue: String = s"\n <${s.stringValue} ${p.stringValue} ${o.stringValue} .\n"
}

trait SesameSearcher {
  self:QuadPattern=>

  def contextOrNull: Resource = if(hasContext && !c.isVar) c.asInstanceOf[Resource] else null
  def subjectOrNull: Resource = if(s.isVar) null else this.asInstanceOf[Resource]
  def propertyOrNull: URI = if(p.isVar) null else this.asInstanceOf[URI]
  def objectOrNull: Value = if(o.isVar) null else this.asInstanceOf[Value]

}

trait QuadPattern extends TripletPattern{
  def hasContext = c!=null
  def c:IRIPatEl
  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")

}


trait ResourcePatEl extends PatternElement
trait IRIPatEl extends ResourcePatEl

/*
 TODO: add binding for vars
 */
trait PatternElement extends GroupElement
{
  def isVar:Boolean = false
  def isIRI:Boolean = false
  def isLiteral = false
  def isBlankNode = false


}