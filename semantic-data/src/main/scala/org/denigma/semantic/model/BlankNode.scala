package org.denigma.semantic.model
import org.openrdf.model.BNode

case class BlankNode(id:String) extends BNode with Res{

  override def stringValue: String = id

  override def getID: String = id


}
