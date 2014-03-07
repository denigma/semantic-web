package org.denigma.semantic.sparql

import org.openrdf.model.{Resource, Literal, Value, URI}
import org.denigma.semantic.model.BlankNode



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

    override def valueOrNull: Value =  null

    override def IRIorNull: URI =  null

    override def resourceOrNull: Resource = null

    override def canBind(value: Value): Boolean = true
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

    override def valueOrNull: Value = u

    override def IRIorNull: URI = u

    override def resourceOrNull: Resource = u

    override def canBind(value: Value): Boolean = u==value
  }

  implicit class BNodeExtended(bnode:BlankNode) extends ResourcePatEl{
    override def isBlankNode = true

    override def stringValue: String = bnode.stringValue()

    override def resourceOrNull: Resource = bnode

    override def valueOrNull: Value = bnode

    override def canBind(value: Value): Boolean = value == bnode
  }

  implicit class LiteralExtended(literal:Literal) extends ValuePatEl
  {


    override def isLiteral = true

    override def stringValue: String = "\""+literal.stringValue+"\""

    override def valueOrNull: Value = literal

    override def canBind(value: Value): Boolean =(value==literal)
  }



}