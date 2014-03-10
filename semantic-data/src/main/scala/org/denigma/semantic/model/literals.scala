package org.denigma.semantic.model

import org.openrdf.model.impl.LiteralImpl
import org.openrdf.model.vocabulary.{XMLSchema=>xe}
import org.openrdf.model.{Literal, URI}
import java.math.{BigDecimal, BigInteger}
import javax.xml.datatype.XMLGregorianCalendar
import org.openrdf.model.datatypes.XMLDatatypeUtil
import org.joda.time.{LocalDate, DateTime}


case class LitStr(value:String) extends LiteralImpl(value,xe.STRING)

case class LitString(value:String, language:String) extends LiteralImpl(value,language)
case class LitDouble(value:Double) extends LiteralImpl(value.toString,xe.DOUBLE)
case class LitLong(value:Long) extends LiteralImpl(value.toString,xe.LONG)
//
//
////TODO: rewrite
//case class LitDate(value:LocalDate) extends TypedLiteral(value.toString,xe.DATE)
//case class LitDateTime(value:DateTime) extends TypedLiteral(value.toString,xe.DATETIME)


//
//  def isString = true
//  def isDouble = false
//  def is
//
//
//}

//case class LitString(value:String) extends LiteralImpl(value,xe.STRING) {
//
//
//}
//
//case class LitDoubgle(value:Double) extends LiteralImpl(value.toString,xe.DOUBLE) {
//
//  override def isString = false
//
//}
