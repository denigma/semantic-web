package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.SortedMap

/**
 * Suggession
 * @param list list of suggessions
 * @param field field from where to suggest
 */
case class Suggestion(list:List[String], field:String)


trait SuggestionFormatter {
  /**
   * JSON reader for search
   */
  implicit val readSuggestion: Reads[Suggestion] = ((__ \ "list").read[List[String]] ~ (__ \ "field").read[String])(Suggestion)
  /**
   * JSON writer for search
   */
  implicit val writeSuggestion:Writes[Suggestion] = (
    (__ \ "list").write[List[String]]
      ~ (__ \ "field").write[String])(unlift(Suggestion.unapply))

  /**
   * JSON format for search
   */
  implicit val suggestionFormat: Format[Suggestion] = Format(readSuggestion, writeSuggestion)

}

/*

case class Suggestion2(list:List[(String,String)], field:String)


trait SuggestionFormatter2 {
  /**
   * JSON reader for search
   */
  implicit val readSuggestion: Reads[Suggestion2] = ((__ \ "list").read[List[(String,String)]] ~ (__ \ "field").read[String])(Suggestion2)
  /**
   * JSON writer for search
   */
  implicit val writeSuggestion:Writes[Suggestion2] = (
    (__ \ "list").write[List[(String,String)]]
      ~ (__ \ "field").write[String])(unlift(Suggestion2.unapply))

  /**
   * JSON format for search
   */
  implicit val suggestionFormat: Format[Suggestion2] = Format(readSuggestion, writeSuggestion)

}
*/