package org.denigma.semantic.sparql

import org.denigma.semantic.sparql.Trip


/**
 * Sparql brackets
 * @param elements elements included
 */
case class Br(elements:GroupElement*) extends GP
{

  override def stringValue = "\n{"+this.foldChildren+" }"

  override lazy val children: List[GroupElement] = elements.toList
}

/**
Group of elements
 */
trait GP extends GroupElement{

  def children:List[GroupElement]

  def hasChildren = !children.isEmpty

  def onlyOneChild = children.size ==1

  def foldChildren: String = children.foldLeft("")((acc,el)=>acc+" "+el.stringValue)

  def UNION(other:GP):Union = Union(this,other)
}


/**
unites to groups together
 */
case class Union(left:GroupElement,right:GroupElement) extends GP {

  override def stringValue = s" ${left.stringValue} UNION ${right.stringValue}"

  override val children = left::right::Nil


}

case class Optional(gp:GroupElement) extends GP
{
  def stringValue = "\n OPTIONAL "+ gp.toString

  override val children = gp::Nil

}

trait GroupElement {

  override def toString = this.stringValue
  def stringValue:String

}

trait VarContainer extends GroupElement{

 var vars = Map.empty[String,Variable]

}
