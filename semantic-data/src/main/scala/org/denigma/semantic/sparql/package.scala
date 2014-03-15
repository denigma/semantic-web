package org.denigma.semantic

import org.denigma.semantic.model.{QueryElement, BlankNode, IRI}
import org.openrdf.model.{Resource, URI}

/**
 * contains important implicits
 */
package object sparql extends PatternImplicits{



  case class EqualsFilter(left:Variable,right:Any) extends Filter {
    override def toString = left.toString+" = "+right.toString
  }


  case class Variable(name:String) {
    override def toString = stringValue

    def stringValue = s"?$name"
  }

  class Filter extends QueryElement
  {
    def stringValue = "FILTER"
  }


  trait TreeElement[T] {
    def parent:T
  }



}
