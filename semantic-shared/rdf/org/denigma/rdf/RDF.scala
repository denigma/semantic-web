package org.denigma.rdf

abstract class WebResource extends RDFValue{

}

case class WebIRI(url:String) extends WebResource
{
  def stringValue = url
}

case class WebBlankNode(id:String) extends WebResource
{
  def stringValue: String = id
}



case class BooleanLiteral(value:Boolean)
{
  def stringValue: Boolean = value
}


case class NumberLiteral(value:Double)
{
  def stringValue: Double = value
}


case class StringLiteral(value:String)
{
  def stringValue: String = value
}

abstract class Literal extends RDFValue

/**
 * Shared
 */
trait RDFValue
{
  def stringValue:String
}

