package org.denigma.semantic.model
import org.openrdf.model._
import org.denigma.semantic.model.IRI

/*
implementation of Resource
 */
trait Res extends RDFValue with Resource{
  override def stringValue: String = this.toString
}

object Res {

  def apply(res:Resource): Res = res match {
    case r:IRI=>r
    case r:URI=>IRI(r.stringValue())
    case b:BlankNode => b
    case b:BNode => BlankNode(b.getID)
  }
}