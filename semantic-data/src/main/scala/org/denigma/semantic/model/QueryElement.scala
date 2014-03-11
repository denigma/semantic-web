package org.denigma.semantic.model

trait QueryElement {

  override def toString = this.stringValue
  def stringValue:String

}