package org.denigma.semantic.classes

import org.openrdf.model.vocabulary.XMLSchema
import org.openrdf.model.{URI, Literal}
import javax.xml.datatype.XMLGregorianCalendar
import org.joda.time._


object StringLiteral extends LiteralExtractor[String](XMLSchema.STRING,XMLSchema.NORMALIZEDSTRING)
{
  override def unapply(lit: Literal) = if(types.contains(lit.getDatatype)) Some(lit.stringValue()) else None
}

object DoubleLiteral extends LiteralExtractor[Double](XMLSchema.DOUBLE,XMLSchema.FLOAT)
{
  override def unapply(lit: Literal): Option[Double] = if(types.contains(lit.getDatatype)) Some(lit.doubleValue()) else None
}

object DecimalLiteral extends LiteralExtractor[BigDecimal](XMLSchema.DECIMAL)
{
  override def unapply(lit: Literal): Option[BigDecimal] = if(types.contains(lit.getDatatype)) Some(lit.decimalValue()) else None
}


/**
 * Matches {@link XMLGregorianCalendar} value of this literal. A calendar
 * representation can be given for literals whose label conforms to the
 * syntax of the following <a href="http://www.w3.org/TR/xmlschema-2/">XML
 * Schema datatypes</a>: <tt>dateTime</tt>, <tt>time</tt>,
 * <tt>date</tt>, <tt>gYearMonth</tt>, <tt>gMonthDay</tt>,
 * <tt>gYear</tt>, <tt>gMonth</tt> or <tt>gDay</tt>.
 *
 * @return The calendar value of the literal.
 * @throws IllegalArgumentException
	 * If the literal cannot be represented by a
 *   { @link XMLGregorianCalendar}.
 */
object CalendarLiteral extends LiteralExtractor[XMLGregorianCalendar](XMLSchema.TIME,XMLSchema.DATETIME,XMLSchema.DATE,XMLSchema.GMONTH,XMLSchema.GMONTHDAY,XMLSchema.GYEAR,XMLSchema.GYEARMONTH,XMLSchema.GDAY)
{
  override def unapply(lit: Literal): Option[XMLGregorianCalendar] = if(types.contains(lit.getDatatype)) Some(lit.calendarValue()) else None
}

object DateTimeLiteral extends LiteralExtractor[DateTime](XMLSchema.DATETIME)
{
  override def unapply(lit: Literal): Option[DateTime] = if(types.contains(lit.getDatatype)) {
    Some(new DateTime(lit.calendarValue().toGregorianCalendar))
  } else None
}

object DateLiteral extends LiteralExtractor[LocalDate](XMLSchema.DATE)
{
  override def unapply(lit: Literal): Option[LocalDate] = if(types.contains(lit.getDatatype)) {
   Some(LocalDate.fromCalendarFields(lit.calendarValue().toGregorianCalendar))
  } else None
}

object MonthDayLiteral extends LiteralExtractor[MonthDay](XMLSchema.GMONTHDAY)
{
  override def unapply(lit: Literal): Option[MonthDay] = if(types.contains(lit.getDatatype)) {
    Some(MonthDay.fromCalendarFields(lit.calendarValue().toGregorianCalendar))
  } else None
}


object LongLiteral extends LiteralExtractor[Long](XMLSchema.INT,XMLSchema.INTEGER,XMLSchema.LONG){
  override def unapply(lit: Literal): Option[Long] = if(types.contains(lit.getDatatype)) Some(lit.longValue()) else None
}

object BooleanLiteral extends LiteralExtractor[Boolean](XMLSchema.BOOLEAN){
  override def unapply(lit: Literal): Option[Boolean] = if(types.contains(lit.getDatatype)) Some(lit.booleanValue()) else None
}

abstract class LiteralExtractor[T](val types:URI*){
  def unapply(lit: Literal): Option[T]
}
