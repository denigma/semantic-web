package org.denigma.semantic.model
import org.openrdf.model._

/*
implementation of Resource
 */
class Res extends RDFValue with Resource{
  override def stringValue: String = this.toString
}
