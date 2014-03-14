package org.denigma.semantic.model

import org.openrdf.model.{Statement, URI, Value, Resource}
import com.hp.hpl.jena.rdf.model.Literal
import org.denigma.semantic.model.LitString
import scalax.collection.GraphEdge.DiHyperEdge

object Quad {
  def apply(statement:Statement): Quad = statement match {
    case q:Quad=> q
    case st =>
      val cont = st.getContext
      Quad(Res(st.getSubject),IRI(st.getPredicate),st.getObject , if(cont!=null) Res(cont) else null)
  }
}

case class Quad(s:Res,p:IRI,o:Value,c:Res = null) extends DiHyperEdge[Value]((s,p,o,c)) with BasicTriplet{
  override def hasContext: Boolean = c!=null

  override def stringValue: String = s"\n ${s.toString} ${p.toString} ${o.toString}" + (if(hasContext) " "+c.toString+" .\n" else " .\n")

  override def getContext: Resource = c
}

object Trip {
  def apply(statement:Statement): Trip = statement match {
    case q:Trip=> q
    case st =>Trip(Res(st.getSubject),IRI(st.getPredicate),st.getObject)
  }
}

case class Trip(s:Res,p:IRI,o:Value) extends DiHyperEdge[Value]((s,p,o)) with BasicTriplet

trait BasicTriplet extends QueryElement with Statement
{
  def hasContext: Boolean = false

  val s:Res
  val p:IRI
  val o:Value


  def objectString: String = o
  match {
//    case lit:LitStr=> "\""+lit.stringValue()+"\""
//    case lit:LitString=> "\""+lit.stringValue()+"\""
//    case lit:Literal=> "\""+lit.stringValue()+"\""
    case uri:URI=>s"<${uri.stringValue}>"
    case o:Literal if o.getDatatype==null=>"\""+o.stringValue()+"\""
    case o=>o.stringValue()
  }

  override def stringValue: String = s"\n <${s.stringValue}> <${p.stringValue}> $objectString .\n"

  override def getContext: Resource = null

  override def getObject: Value = o

  override def getPredicate: URI = p

  override def getSubject: Resource = s


}
