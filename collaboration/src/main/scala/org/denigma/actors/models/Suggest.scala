package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * suggests a gene
 * @param query letters of the name
 * @param field where to search the name for
 *
 */
case class Suggest(query:String, field:String)

trait SuggestFormatter {
  /**
   * JSON reader for search
   */
  implicit val readSuggest: Reads[Suggest] = ((__ \ "query").read[String] ~ (__ \ "field").read[String])(Suggest)
  /**
   * JSON writer for search
   */
  implicit val writeSuggest:Writes[Suggest] = (
    (__ \ "query").write[String]
      ~ (__ \ "field").write[String])(unlift(Suggest.unapply))

  /**
   * JSON format for search
   */
  implicit val suggestFormat: Format[Suggest] = Format(readSuggest, writeSuggest)

}


