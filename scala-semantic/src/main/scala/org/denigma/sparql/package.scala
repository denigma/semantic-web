package org.denigma

import org.denigma.rdf.{IRIPatEl, QueryElement}

/**
 * Sparql package object
 */
package object sparql {
  def ?(name:String): Variable = Variable(name)


  implicit class AggInSelect(a:Aggregate) extends SelectElement {
    override def isAgg = true

    override def stringValue: String = a.stringValue
  }

}