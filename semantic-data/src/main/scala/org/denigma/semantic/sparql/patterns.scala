package org.denigma.semantic.sparql

import org.denigma.semantic.model.{BlankNode, IRI}
import org.openrdf.model.{Value, Resource, URI}
import com.hp.hpl.jena.rdf.model.Literal


/**
 * Basic graph pattern
 * @param s Subject (Var,Literal or Resource)
 * @param p Predicate (Var,Literal or Resource)
 * @param o Object (Var,Literal or Resource)
 * @param c Context (Var,Literal or Resource or Null)
 */
case class Pat(s:ResourcePatEl,p:IRIPatEl,o:ValuePatEl,c:IRIPatEl= null) extends QuadPattern
case class Trip(s:Resource,p:IRI,o:Value) extends GroupElement
{
  override def stringValue: String = s"\n ${s.stringValue} ${p.stringValue} ${o.stringValue} .\n"
}

trait TripletPattern extends GroupElement
{
  def s:ResourcePatEl
  def p:IRIPatEl
  def o:ValuePatEl
  override def stringValue: String = s"\n ${s.stringValue} ${p.stringValue} ${o.stringValue} .\n"
}

trait QuadPattern extends TripletPattern{
  def hasContext = c!=null
  def c:PatternElement
  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")
}

/**
 * Trait that keeps patterns inside
 * Is extended by sparql package object
 */
trait PatternImplicits {

  def ?(name:String): Variable = Variable(name)

  implicit class VarExtended(val variable:Variable) extends IRIPatEl with SelectElement  {
    override def isVar = true

    //    def <(value:Double) = new Filter()
    //    def >(value:Double)
    //    def ==(other:Any)  =  EqualsFilter(v,other)
    override def stringValue: String = variable.stringValue

  }

  implicit class AggInSelect(a:Aggregate) extends SelectElement {
    override def isAgg = true

    override def stringValue: String = a.stringValue
  }

  trait SelectElement extends GroupElement{
    def isVar:Boolean = false
    def isAgg:Boolean = false
  }

  implicit class IriExtended(u:URI) extends IRIPatEl
  {
    override def isIRI = true
    def stringValue:String = "<"+u.stringValue+">"
  }

  implicit class BNodeExtended(bnode:BlankNode) extends ResourcePatEl{
    override def isBlankNode = true

    override def stringValue: String = bnode.stringValue()
  }

  implicit class LiteralExtended(literal:Literal) extends ResourcePatEl
  {
    override def isLiteral = true

    override def stringValue: String = literal.stringValue
  }


  trait ResourcePatEl extends PatternElement
  trait IRIPatEl extends ResourcePatEl
  type ValuePatEl = PatternElement

  trait PatternElement extends GroupElement
  {
    def isVar:Boolean = false
    def isIRI:Boolean = false
    def isLiteral = false
    def isBlankNode = false
  }

}