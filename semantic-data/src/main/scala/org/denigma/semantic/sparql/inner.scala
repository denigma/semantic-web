package org.denigma.semantic.sparql

import org.denigma.semantic.model.QueryElement

case class Variable(name:String) {
  override def toString = stringValue

  def stringValue = s"?$name"
}
case class EqualsFilter(left:Variable,right:Any) extends Filter {
  override def toString = left.toString+" = "+right.toString
}
class Filter extends QueryElement
{
  def stringValue = "FILTER"
}


trait TreeElement[T] {
  def parent:T
}