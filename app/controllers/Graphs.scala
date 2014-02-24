package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.DateTimeZone
import org.denigma.semantic.platform.WithSemanticPlatform


/*
coopy-pasted from one of previous projects, should be changed a lot
 */
object Graphs extends WithSemanticPlatform with GenGraph{

  DateTimeZone.setDefault(DateTimeZone.UTC)

  def index(id:String) = Action {
    implicit request =>


      Ok(views.html.graphs.index(if(id=="")"TestRoot" else id)) //Ok(views.html.page("node","menu","0"))
  }


  def content = Action {
    implicit request =>
      Ok(views.html.graphs.content()) //Ok(views.html.page("node","menu","0"))
  }


  //  def node(id:String) = Action {
  //    implicit request =>
  //      Ok(views.html.graphs.vertex(id)) //Ok(views.html.page("node","menu","0"))
  //  }

  def vertices(id:String) =
    Action {
      implicit request =>  Ok(this.genNodes()).as("application/json")
    }
  /*
  gets incoming edges as json
   */
  def incoming(to:String) =    Action {
    implicit request =>  Ok(this.genEdges).as("application/json")
  }


  /*
  gets outgoing edges as json
   */
  def outgoing(from:String) =     Action {
    implicit request =>  Ok(this.genEdges).as("application/json")
  }



  //  /*
  //gets incoming edges as json
  // */
  //  def incoming(to:String) = Action{
  //    implicit request =>
  //      vo(to) match
  //      {
  //        case None =>Ok(Json.obj("edges" ->  JsNull)).as("application/json")
  //        case Some(vert) =>
  //          val edges = vert.allInV.map{case (label:String,v:Vertex)=>new EdgeViewModel(label, v)}
  //          Ok(Json.obj("edges" -> Json.toJson(edges.map(edge2js)))).as("application/json") //Ok(views.html.page("node","menu","0"))
  //      }
  //  }
  //
  //  /*
  //  gets outgoing edges as json
  //   */
  //  def outgoing(from:String) = Action{
  //    implicit request =>
  //      vo(from) match
  //      {
  //        case None =>Ok(Json.obj("edges" ->  JsNull)).as("application/json")
  //        case Some(vert) =>
  //          val edges = vert.allOutV.map{case (label:String,v:Vertex)=>new EdgeViewModel(label, v)}
  //          Ok(Json.obj("edges" -> Json.toJson(edges.map(edge2js)))).as("application/json") //Ok(views.html.page("node","menu","0"))
  //      }
  //  }



}