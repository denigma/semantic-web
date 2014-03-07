package org.denigma.semantic.sparql

import org.openrdf.model.URI
import org.denigma.semantic.model.BlankNode
import com.hp.hpl.jena.rdf.model.Literal


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

  implicit class LiteralExtended(literal:Literal) extends ValuePatEl
  {


    override def isLiteral = true

    override def stringValue: String = "\""+literal.stringValue+"\""

  }
  type ValuePatEl = PatternElement

}