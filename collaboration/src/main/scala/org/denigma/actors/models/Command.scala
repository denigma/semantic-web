package org.denigma.actors.models

import org.denigma.actors.messages.EventLike
import play.api.libs.json._

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.functional.syntax._

trait CommandLike extends EventLike
case class FreeCommand(name:String) extends CommandLike
object UnknownCommand extends CommandLike{val name = "unknown"}
object GetUsers extends CommandLike{val name = "getUsers"}

trait CommandFormatter {


  implicit val readCommand: Reads[CommandLike] = new Reads[CommandLike] {
    def reads(json: JsValue): JsResult[CommandLike] = (json \ "name").asOpt[String] match
    {
      case None=>JsSuccess(UnknownCommand)
      case Some(str)=>JsSuccess(FreeCommand(str))
    }
  }

  implicit val writeCommand:Writes[CommandLike] = new Writes[CommandLike] {
    def writes(o: CommandLike): JsValue =  Json.obj(
      "name" -> o.name
    )
  }
  /**
   * JSON format for search
   */
  implicit val CommandFormat: Format[CommandLike] = Format(readCommand, writeCommand)

}