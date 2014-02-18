package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.denigma.actors.models.IdOwner

/**
 * Includes search query
 * @param id search id
 * @param query search query
 */
case class Search(id:String,query:String)  extends IdOwner

trait SearchFormatter {
  /**
   * JSON reader for search
   */
  implicit val readSearch: Reads[Search] = ((__ \ "id").read[String] ~ (__ \ "query").read[String])(Search)
  /**
   * JSON writer for search
   */
  implicit val writeSearch:Writes[Search] = (
    (__ \ "id").write[String]
      ~ (__ \ "query").write[String])(unlift(Search.unapply))

  /**
   * JSON format for search
   */
  implicit val searchFormat: Format[Search] = Format(readSearch, writeSearch)

}


