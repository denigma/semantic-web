package controllers

import play.api.mvc._

import play.api.libs.json.Json
import views.html.index
import auth.UserAction
import play.twirl.api.Html

class PJaxPlatformWith(val name:String) extends Controller  {

  def index(): Action[AnyContent] =  UserAction {
    implicit request=>
      this.index(request)
  }

  def index[T<:UserRequestHeader](request:T,someHtml:Option[Html] = None) = {
    Ok(views.html.index(request,someHtml))
  }


//  def pj[T](action:String,html:Html)(implicit req:UserRequestHeader): Html =
//    pj(name,action:String,html:Html)(req)

//  def pj[T](controller:String,action:String,html:Html)(implicit req:UserRequestHeader): Html =
//    if(req.headers.keys("X-PJAX")) html  else views.html.webintelligence.index(controller,action,html)(req)

  def pj[T<:UserRequestHeader](html:Html)(implicit request:T): Result =
    if(request.pjax.isEmpty) this.index(request,Some(html)) else  Ok(html)

  def tellBad(message:String) = BadRequest(Json.obj("status" ->"KO","message"->message)).as("application/json")

}
