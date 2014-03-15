package org.denigma.semantic.model
import org.openrdf.model._


object IRI {

  def apply(res:URI): IRI = res match {
    case r:IRI=>r
    case r:URI=>IRI(r.stringValue())
  }

}
/*
implementation of openrdf URI class
 */
case class IRI(uri:String) extends Res with URI
{

  require(uri.contains(":"), "uri string must by URL")

  lazy val lastIndex = Math.max(uri.lastIndexOf("#"),uri.lastIndexOf("/"))

  override lazy val getLocalName: String = uri.substring(lastIndex)

  override lazy val getNamespace: String = uri.substring(0,this.lastIndex)

  override lazy val stringValue: String = this.uri

  override def toString: String = "<"+uri+">"

  override def hashCode: Int = uri.hashCode




//  override val isIRI = true
//
//  override val isVar = false
}
///**
// * in order to avoid either in graph patterns
// */
//trait VarOrIRI{
//  def isVar:Boolean
//  def isIRI:Boolean
//}