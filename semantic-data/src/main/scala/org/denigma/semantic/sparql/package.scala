package org.denigma.semantic

import org.denigma.semantic.model.IRI
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.PatternElements
import org.openrdf.model.URI

/**
 * contains important implicits
 */
package object sparql {


  implicit class VarExtended(v:Variable) extends SelectElement with PatternElement {
    override def isVar = true

//    def <(value:Double) = new Filter()
//    def >(value:Double)
//    def ==(other:Any)  =  EqualsFilter(v,other)
    override def stringValue: String = v.stringValue


  }

  implicit class AggInSelect(a:Aggregate) extends SelectElement {
    override def isAgg = true

    override def stringValue: String = a.stringValue
  }

  trait SelectElement extends GroupElement{
    def isVar:Boolean = false
    def isAgg:Boolean = false
  }

  implicit class IriExtended(u:URI) extends PatternElement
  {
    override def isIRI = true
    def stringValue:String = "<"+u.stringValue+">"

  }

  trait PatternElement extends GroupElement
  {
    def isVar:Boolean = false
    def isIRI:Boolean = false
  }


  def ?(name:String): Variable = Variable(name)




  //implicit def ==() = ""


  case class EqualsFilter(left:Variable,right:Any) extends Filter {
    override def toString = left.toString+" = "+right.toString
  }


  case class Variable(name:String) {
    override def toString = stringValue

    def stringValue = s"?$name"
  }

  class Filter extends GroupElement
  {
    def stringValue = "FILTER"
  }


  trait TreeElement[T] {
    def parent:T
  }



}
