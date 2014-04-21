package controllers

import org.denigma.semantic.controllers.UpdateController
import org.scalajs.spickling.PicklerRegistry
import play.api.mvc._
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.files.SemanticFileParser
import play.api.libs.json.{JsValue, JsObject}
import play.api.templates.Html
import models.MenuItem
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}
import org.scalax.semweb.rdf.IRI

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
