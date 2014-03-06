package org.denigma.semantic

import org.openrdf.model.Literal
import org.openrdf.model.vocabulary.{XMLSchema=>xe}

/**
 * implicits for models
 */
package object model {


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
