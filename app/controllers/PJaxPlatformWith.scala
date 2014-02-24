package controllers

import play.api.mvc._

import play.api.templates.Html
import org.denigma.semantic.platform.WithSemanticPlatform

class PJaxPlatformWith(val name:String) extends Controller with WithSemanticPlatform {

  def pj[T](action:String,html:Html)(implicit req:Request[T]): Html =
    pj(name,action:String,html:Html)(req)

  def pj[T](controller:String,action:String,html:Html)(implicit req:Request[T]): Html =
    if(req.headers.keys("X-PJAX")) html  else views.html.index(controller,action,html)




}
