package org.denigma.video.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.denigma.actors.models.{Search, IdOwner}

/**
 * Opens frames and shows to other users
 * @param id id of an iframe
 * @param url url to open
 */
case class Frame(id:String,url:String) extends IdOwner

trait FrameFormatter {
  /**
   * JSON reader for frame
   */
  implicit val readFrame: Reads[Search] = ((__ \ "id").read[String] ~ (__ \ "url").read[String])(Search)
  /**
   * JSON writer for frame
   */
  implicit val writeFrame:Writes[Search] = (
    (__ \ "id").write[String]
      ~ (__ \ "url").write[String])(unlift(Search.unapply))

  /**
   * JSON format for frame
   */
  implicit val searchFormat: Format[Search] = Format(readFrame, writeFrame)

  /**
   * Regex to check URL validity
   */
  val regex_url = "^[A-Za-z][A-Za-z0-9+.-]{1,120}:[A-Za-z0-9/](([A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2}){1,333}(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?$"

  /**
   * Checks if url is valid
   * @param url url to be checked
   * @return true if valid and false if not
   */
  def isValidUrl(url:String): Boolean =url.matches(regex_url)

}

