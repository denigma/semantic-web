package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Parses request info from the client
 * @param channel channel from which it was received, usually a clientside collection i.e. messages, users etc.
 * @param content content of the request
 * @param request type of the request, can be read, save, delete etc.
 */
case class RequestInfo(channel:String,content:JsValue, request:String, room:String="all")

//TODO: rewrite requestInfoFormat and other formats with optional params
trait RequestInfoFormatter
{
  implicit val readRequestInfo: Reads[RequestInfo] = ((__ \ "channel").read[String] ~ (__ \ "content").read[JsValue]~  (__ \ "request").read[String] ~ (__ \ "room").read[String])(RequestInfo)
  implicit val writeRequestInfo:Writes[RequestInfo] = (
    (__ \ "channel").write[String]
      ~ (__ \ "content").write[JsValue]
      ~ (__ \ "request").write[String]
      ~ (__ \ "room").write[String])(unlift(RequestInfo.unapply))

  implicit val requestInfoFormat: Format[RequestInfo] = Format(readRequestInfo, writeRequestInfo)

}
