package controllers

import play.api.mvc._

import play.api.templates.Html
import play.api.libs.json.Json

class PJaxPlatformWith(val name:String) extends Controller  {

  def pj[T](action:String,html:Html)(implicit req:UserRequestHeader): Html =
    pj(name,action:String,html:Html)(req)

  def pj[T](controller:String,action:String,html:Html)(implicit req:UserRequestHeader): Html =
    if(req.headers.keys("X-PJAX")) html  else views.html.webintelligence.index(controller,action,html)(req)


  def tellBad(message:String) = BadRequest(Json.obj("status" ->"KO","message"->message)).as("application/json")

}
