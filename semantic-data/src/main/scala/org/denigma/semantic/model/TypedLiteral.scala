package org.denigma.semantic.model

import org.openrdf.model.{URI, Literal}
import org.openrdf.model.datatypes.XMLDatatypeUtil
import java.math.{BigDecimal, BigInteger}
import javax.xml.datatype.XMLGregorianCalendar

import org.openrdf.model.vocabulary.{XMLSchema=>xe}

//abstract class TypedLiteral(label:String, val datatype:URI) extends Literal with TypedLit {
//
//
//  def language: String = null
//
//  def getLabel: String = label
//
//
//
//  def getLanguage: String = language
//
//
//  def getDatatype: URI = datatype
//
//  /**
//   * TODO: rewrite this sesame code in Scala stype
//   * @param o
//   * @return
//   */
//  override def equals(o: AnyRef): Boolean = {
//    if (this eq o) {
//      return true
//    } else
//      o match {
//        case other: Literal =>
//          if (!(label == other.getLabel)) {
//            return false
//          }
//          if (datatype == null) {
//            if (other.getDatatype != null) {
//              return false
//            }
//          }
//          else {
//            if (!(datatype == other.getDatatype)) {
//              return false
//            }
//          }
//          if (language == null) {
//            if (other.getLanguage != null) {
//              return false
//            }
//          }
//          else {
//            if (!(language == other.getLanguage)) {
//              return false
//            }
//          }
//          return true
//        case _ =>
//      }
//    false
//  }
//
//  override def hashCode: Int = label.hashCode
//
//
//  /**
//   * Returns the label of the literal.
//   */
//  override def toString: String = {
//    val sb: StringBuilder = new StringBuilder(label.length * 2)
//    sb.append('"')
//    sb.append(label)
//    sb.append('"')
//    if (language != null) {
//      sb.append('@')
//      sb.append(language)
//    }
//    if (datatype != null) {
//      sb.append("^^<")
//      sb.append(datatype.toString)
//      sb.append(">")
//    }
//    sb.toString()
//  }
//
//  def stringValue: String =   label
//
//  def booleanValue: Boolean = XMLDatatypeUtil.parseBoolean(getLabel)
//
//  def byteValue: Byte = XMLDatatypeUtil.parseByte(getLabel)
//
//  def shortValue: Short = XMLDatatypeUtil.parseShort(getLabel)
//
//
//  def intValue: Int = XMLDatatypeUtil.parseInt(getLabel)
//
//  def longValue: Long = XMLDatatypeUtil.parseLong(getLabel)
//
//  def floatValue: Float = XMLDatatypeUtil.parseFloat(getLabel)
//
//  def doubleValue: Double = XMLDatatypeUtil.parseDouble(getLabel)
//
//
//  def integerValue: BigInteger = XMLDatatypeUtil.parseInteger(getLabel)
//
//  def decimalValue: BigDecimal = XMLDatatypeUtil.parseDecimal(getLabel)
//
//  def calendarValue: XMLGregorianCalendar = XMLDatatypeUtil.parseCalendar(getLabel)
//
//  /*
//   * TODO: rewrite
//   */
//  override def isCalendar: Boolean =
//    getDatatype == xe.DATETIME ||
//      getDatatype == xe.DATE ||
//      getDatatype == xe.GMONTH ||
//      getDatatype == xe.GMONTHDAY ||
//      getDatatype == xe.GDAY ||
//      getDatatype == xe.GYEAR ||
//      getDatatype == xe.TIME ||
//      getDatatype == xe.DAYTIMEDURATION ||
//      getDatatype == xe.GYEARMONTH
//
//
//  override def isDecimal: Boolean = getDatatype == xe.DECIMAL
//
//  override def isInteger: Boolean = getDatatype == xe.INTEGER
//
//  override def isDouble: Boolean = getDatatype == xe.DOUBLE
//
//  override def isFloat: Boolean = getDatatype == xe.FLOAT
//
//  override def isLong: Boolean = getDatatype == xe.LONG
//
//  override def isInt: Boolean = getDatatype == xe.INT
//
//  override def isShort: Boolean = getDatatype == xe.SHORT
//
//  override def isByte: Boolean = getDatatype == xe.BYTE
//
//  override def isBoolean: Boolean = getDatatype == xe.BOOLEAN
//
//  override def isString: Boolean = getDatatype == xe.STRING || getDatatype == xe.NORMALIZEDSTRING
//
//  override def isAny: Boolean = getDatatype == null
//
//
//
//}
/*
 * TODO: rewrite
 */
trait TypedLit 
{

  def isBoolean :Boolean
  def isByte :Boolean
  def isShort :Boolean
  def isInt :Boolean
  def isLong :Boolean
  def isFloat :Boolean
  def isDouble :Boolean
  def isInteger :Boolean
  def isDecimal :Boolean
  def isCalendar :Boolean
  def isString:Boolean
  def isAny: Boolean

}
