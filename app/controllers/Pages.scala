package controllers

import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}
import play.api.templates.Html
import play.api.mvc.Action
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerMaterializersImpl._
import org.scalajs.spickling.playjson._
import models.RegisterPicklers._
import play.api.mvc._
import java.io.File
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.files.SemanticFileParser
import org.denigma.semantic.users.Accounts
import scala.util._
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import play.api.templates.Html
import org.scalajs.spickling.PicklerRegistry
import models.{RegisterPicklers, MenuItem, Menu}
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController, QueryController}
import org.denigma.rdf.model.IRI

/**
 * Pages controller
 */
object Pages extends Controller with SimpleQueryController with UpdateController{


  def page(uri:String)= UserAction {
    implicit request=>
      Ok(Html(s"<h1>$uri<h1>"))
  }

  def view(viewName:String) = UserAction {
    implicit request=>
      Ok(Html(s"<h1>$viewName<h1>"))
  }

}
