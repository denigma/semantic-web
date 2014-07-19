package controllers

import org.denigma.binding.picklers.{BindingPicklers, rp}
import org.scalajs.spickling.playjson._
import org.scalax.semweb.picklers.MapRegistry
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._


trait ItemsController extends PickleController{
  self: Controller=>

  type ModelType //<:Model


  def all(): Action[AnyContent]

  def add(): Action[ModelType]

  def delete(): Action[ModelType]



}

trait PickleController {
  self:Controller=>
 /*
  **
  * Generates body parser for required type
  * @param failMessage
    * @param register
    * @tparam T
    * @return
  */
  def pickle[T](failMessage:String = "cannot unpickle json data")(implicit registry:MapRegistry with BindingPicklers = rp)  = parse.tolerantJson.validate[T]{
    case value: JsValue =>
      registry.unpickle(value)   match {
        case null=>Left(BadRequest(Json.obj("status" ->"KO","message"->failMessage)).as("application/json"))
        case data:T=>Right(data)
        case _=>Left(BadRequest(Json.obj("status" ->"KO","message"->"some UFO data")).as("application/json"))

      }
  }
}