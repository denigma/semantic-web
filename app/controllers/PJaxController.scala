package controllers

import play.api.mvc._
import org.openrdf.model.impl.URIImpl
import org.openrdf.repository.RepositoryResult
import org.denigma.semantic.data.{LoveHater, SG}
import SG.db
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.model._
import java.io.File
import org.openrdf.rio.RDFFormat
import play.api.libs.json.{JsObject, Json}
import play.api.Play
import play.api.Play.current
import play.api.templates.{HtmlFormat, Html}

class PJaxController(val name:String) extends Controller{

  def pj[T](action:String,html:Html)(implicit req:Request[T]): Html =
    pj(name,action:String,html:Html)(req)

  def pj[T](controller:String,action:String,html:Html)(implicit req:Request[T]): Html =
    if(req.headers.keys("X-PJAX")) html  else views.html.index(controller,action,html)




}
