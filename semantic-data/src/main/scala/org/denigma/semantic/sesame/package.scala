package org.denigma.semantic

import org.openrdf.model._
import org.openrdf.model.impl._
import org.openrdf.model.impl.{BNodeImpl, LiteralImpl, URIImpl}
import org.openrdf.model.vocabulary.{XMLSchema=>xe}
import org.denigma.rdf._
import org.denigma.rdf._
import org.openrdf.model.Literal
import org.denigma.rdf.sparql
import org.denigma.rdf.model._
import org.denigma.rdf.model.IRI

/**
 * Sesame extensions
 */
package object sesame extends Scala2SesameModelImplicits with Sesame2ScalaModelImplicits {

  /*
  object Quad {
  def apply(statement:Statement): Quad = statement match {
    case q:Quad=> q
    case st =>
      val cont = st.getContext
      Quad(st.getSubject,st.getPredicate,st.getObject , if(cont!=null) cont else null)
  }
}
object Trip {
  def apply(statement:Statement): Trip = statement match {
    case q:Trip=> q
    case st =>Trip(st.getSubject,st.getPredicate,st.getObject)
  }
}

   */


}


/**
 * Implicit conversions from Scala-Semantic 2 Sesame
 */
trait Scala2SesameModelImplicits{

  implicit def IRI2URI(iri:IRI) = new URIImpl(iri.stringValue)
  implicit def blankNode2BNode(b:BlankNode) = new BNodeImpl(b.stringValue)
  implicit def res2Resource(res:Res): Resource = res match {
    case null=>null
    case res:Resource=>res
    case iri:IRI=>this.IRI2URI(iri)
    case b:BlankNode=>this.blankNode2BNode(b)
  }

  implicit def rdfValue2Value(v:RDFValue): Value =v match {
    case null=>null
    case res:Resource=>res
    case iri:IRI=>this.IRI2URI(iri)
    case b:BlankNode=>this.blankNode2BNode(b)
    case l:Lit=>this.lit2Literal(l)
  }



  def lit2Literal(lit:Lit):Literal = lit match {
    case null=>null
    case StringLangLiteral(text,lang)=>new LiteralImpl(text,lang)
    case lit:DatatypeLiteral=> new LiteralImpl(lit.label, lit.dataType)
    case other=>new LiteralImpl(other.label)

  }



}

/**
 * Implicit conversions from Sesame to Scala-Semantic
 */
trait Sesame2ScalaModelImplicits{
  implicit def URI2IRI(uri:URI) = if(uri==null) null else IRI(uri.stringValue)
  implicit def Resource2Res(r:Resource)  = r match {
    case null=>null
    case b:BNode=>BlankNode(b.getID)
    case u:URI=>this.URI2IRI(u)
  }

  implicit def Value2RDFValue(value:Value):RDFValue = value match {
    case null=>null
    case uri:URI=>URI2IRI(uri)
    case b:BNode=>Resource2Res(b)
    case res:Resource=>Resource2Res(res)
    case l:Literal=>literal2Lit(l)
  }


   def isCalendar(l:Literal): Boolean =  l.getDatatype == xe.DATETIME ||
      l.getDatatype == xe.DATE ||
      l.getDatatype == xe.GMONTH ||
      l.getDatatype == xe.GMONTHDAY ||
      l.getDatatype == xe.GDAY ||
      l.getDatatype == xe.GYEAR ||
      l.getDatatype == xe.TIME ||
      l.getDatatype == xe.DAYTIMEDURATION ||
      l.getDatatype == xe.GYEARMONTH

  implicit def literal2Lit(l:Literal):Lit = l.getDatatype match {
    case null=>if(l==null) null else AnyLit(l.getLabel)
    case xe.BOOLEAN => BooleanLiteral(l.booleanValue())
    case xe.DECIMAL => DecimalLiteral(l.decimalValue())
    case xe.DOUBLE => DoubleLiteral(l.doubleValue())
    case xe.LONG => LongLiteral(l.longValue())
    case xe.INT => IntegerLiteral(l.intValue())
    case xe.STRING | xe.NORMALIZEDSTRING=> if(l.getLanguage!="") StringLangLiteral(l.getLabel,l.getLanguage) else StringLiteral(l.getLabel)
    case other => AnyLit(l.getLabel)
  }



  implicit def Statement2Quad(st:Statement) =
    new Quad(
      Resource2Res(st.getSubject),
      URI2IRI(st.getPredicate),
      Value2RDFValue(st.getObject),
      Resource2Res(st.getContext))


  implicit def URI2PatEl(uri:URI):IRIPatEl = IRI(uri.stringValue)

  //implicit def URI2ResPatEl(uri:URI):ResourcePatEl = IRI(uri.stringValue)


  implicit def BNode2PatEl(bn:BNode):BNodePatEl = BlankNode(bn.stringValue)

  implicit def Value2PatEl(value:Value):ValuePatEl = value match {
    case null=>null
    case lit:Literal=>literal2Lit(lit)
    case b:BNode=>this.BNode2PatEl(b)
    case iri:URI => URI2IRI(iri)
    case _ =>null
  }

  implicit def Resource2PatEl(r:Resource):ResourcePatEl  = r match {
    case null=>null
    case b:BNode=>this.BNode2PatEl(b)
    case iri:URI => URI2IRI(iri)
    case _ =>null
  }

}