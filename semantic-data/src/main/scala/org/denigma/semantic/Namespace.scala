package org.denigma.semantic

/*
case class for namespaces
 */
case class Namespace(name:String,prefix:String) extends org.openrdf.model.Namespace{
  override def getPrefix: String = prefix

  override def getName: String = name
}