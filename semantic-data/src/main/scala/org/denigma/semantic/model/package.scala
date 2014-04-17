package org.denigma.semantic

import org.openrdf.model._
import org.openrdf.model.vocabulary.{XMLSchema=>xe}
import org.openrdf.model.impl.{StatementImpl, LiteralImpl, URIImpl}
import org.denigma.semantic.model._
import org.openrdf.model.vocabulary.{XMLSchema=>xe}

/**
 * implicits for models
 */
package object model extends Scala2SesameModelImplicits with Sesame2ScalaModelImplicits{




  implicit class LiteralExtended(l:Literal) extends TypedLit{
    override def isCalendar: Boolean = 
      l.getDatatype == xe.DATETIME ||
        l.getDatatype == xe.DATE ||
        l.getDatatype == xe.GMONTH ||
        l.getDatatype == xe.GMONTHDAY ||
        l.getDatatype == xe.GDAY ||
        l.getDatatype == xe.GYEAR ||
        l.getDatatype == xe.TIME ||
        l.getDatatype == xe.DAYTIMEDURATION ||
        l.getDatatype == xe.GYEARMONTH
        
    
    override def isDecimal: Boolean = l.getDatatype == xe.DECIMAL

    override def isInteger: Boolean = l.getDatatype == xe.INTEGER

    override def isDouble: Boolean = l.getDatatype == xe.DOUBLE

    override def isFloat: Boolean = l.getDatatype == xe.FLOAT

    override def isLong: Boolean = l.getDatatype == xe.LONG

    override def isInt: Boolean = l.getDatatype == xe.INT

    override def isShort: Boolean = l.getDatatype == xe.SHORT

    override def isByte: Boolean = l.getDatatype == xe.BYTE

    override def isBoolean: Boolean = l.getDatatype == xe.BOOLEAN

    override def isString: Boolean = l.getDatatype == xe.STRING || l.getDatatype == xe.NORMALIZEDSTRING

    override def isAny: Boolean = l.getDatatype == null

    

  }
}

trait Scala2SesameModelImplicits{
  implicit def IRI2URI(iri:IRI) = new URIImpl(iri.stringValue)
  implicit def LitStr2Literal(lit:LitStr) = new LiteralImpl(lit.value,xe.STRING)
  implicit def LitDouble2Literal(lit:LitDouble) = new LiteralImpl(lit.stringValue,xe.DOUBLE)
  implicit def LitLong2Literal(lit:LitLong) = new LiteralImpl(lit.stringValue,xe.LONG)


}


trait Sesame2ScalaModelImplicits{
  implicit def URI2IRI(uri:IRI) = IRI(uri.stringValue)
}