package org.denigma.actors.staff

import play.api.libs.json.JsValue
import java.util.Date


/**
 * Does log messaging for some typical errors
 */
trait  LogActor extends NamedActor{
  def logUnregisteredInput(some:Any) =
    log.error(s"$name: unregistered RECEIVED input ${some.toString}")

  def logReceived(some:Any) =
    log.error(s"$name: unregistered RECEIVED input ${some.toString}")

  def logReceivedJson(value:JsValue)(implicit date:Date) =
    log.debug(s"$name : RECEIVED JSON: ${value.toString()} at ${date.toString} ")

  def logWrongParsingTypeFor(fr:String) =
    log.error(s"$name: wrong PARSING type for $fr")


  def logWrongRequestInfoParsing(implicit date:Date) =
    log.error(s"$name: ERROR in PARSING RequestInfo json: unable to do parseOperations at ${date.toString} ")

  def logCannotFindRoom(room:String) =
    this.log.error(s"$name : ROOM 404 ERROR, cannot find room ' $room '")

  def logNotValid(what:String) = log.info(s"$name : cannot add $what because it is not valid")

  def logRequestIsNotImplemented(tp:String,req:String) =
    this.log.info(s"$name: $tp request '$req' is NOT IMPLEMENTED")

  def logOtherInfo() =
    log.debug("Info to others was told")

  def logSend(value:JsValue)(implicit date:Date) =
    log.debug(s"$name SEND json: ${value.toString()} at ${date.toString}")

  def logBinding(channel:String,request:String,content:JsValue) =
    log.debug(s"$name: BINDING has fired for channel = $channel with request = $request and content = ${content.toString()}")

}
