package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import org.scalax.semweb.rdf.{StringLiteral, IRI}
import play.api.libs.json.{Json, JsValue}
import org.scalajs.spickling.playjson._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.vocabulary.WI
import models._
import auth.UserAction

trait ItemsController extends PickleController{
  self: Controller=>

  implicit def register:()=>Unit

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
  def pickle[T](failMessage:String = "cannot unpickle json data")(implicit register: ()=>Unit)  = parse.tolerantJson.validate[T]{
    case value: JsValue =>
      register()
      PicklerRegistry.unpickle(value)
      value match {
        case data:T=>Right(data)
        case null=>Left(BadRequest(Json.obj("status" ->"KO","message"->failMessage)).as("application/json"))
        case _=>Left(BadRequest(Json.obj("status" ->"KO","message"->"some UFO data")).as("application/json"))

      }
  }
}