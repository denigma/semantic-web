package org.denigma.semantic

import org.denigma.semantic.model.{QueryElement, BlankNode, IRI}
import com.hp.hpl.jena.sparql.engine.optimizer.reorder.PatternElements
import org.openrdf.model.{Resource, URI}
import com.hp.hpl.jena.rdf.model.Literal

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
