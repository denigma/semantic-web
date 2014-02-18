package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Chat message
 * @param id id of the message
 * @param user user to whom the message belongs to
 * @param text text of the message
 */
case class Message(id:String,user:String,text:String)  extends IdOwner


trait MessageFormatter {
  /**
   * JSON reader for message
   */
  implicit val readMessage: Reads[Message] = ((__ \ "id").read[String] ~ (__ \ "user").read[String] ~ (__ \ "text").read[String])(Message)
  /**
   * JSON writer for message
   */
  implicit val writeMessage:Writes[Message] = (
    (__ \ "id").write[String]
      ~ (__ \ "user").write[String]
      ~ (__ \ "text").write[String])(unlift(Message.unapply))

  /**
   * JSON format for message
   */
  implicit val messageFormat: Format[Message] = Format(readMessage, writeMessage)

}
