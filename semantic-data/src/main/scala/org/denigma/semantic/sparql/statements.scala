package org.denigma.semantic.sparql

import org.openrdf.model.{URI, Value, Resource}
import com.hp.hpl.jena.rdf.model.Literal

case class Quad(s:Resource,p:URI,o:Value,c:Resource = null) extends GroupElement{
  def hasContext = c!=null

  def objectString = o match {
    case lit:Literal=>"\""+lit.stringValue()+"\""
    case uri:URI=>s"<${uri.stringValue}>"
    case o=>o.stringValue()

  }

  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")

}


case class Trip(s:Resource,p:URI,o:Value) extends GroupElement
{

  def objectString = o match {
    case lit:Literal=>"\""+lit.stringValue()+"\""
    case uri:URI=>s"<${uri.stringValue}>"
    case o=>o.stringValue()

  }

  override def stringValue: String = s"\n <${s.stringValue}> <${p.stringValue}> $objectString .\n"
}
