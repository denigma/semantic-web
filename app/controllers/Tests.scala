package controllers

import play.api.mvc._
import org.openrdf.query.BindingSet
import play.api.libs.json.{JsValue, Json}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.playjson._
import models.RegisterPicklers
import models._
import play.api.Logger
import org.denigma.rdf.WebIRI


/*
test controller
not working yet
 */
object Tests  extends Controller{


  def sigma = Action {
    implicit request =>
      Ok(views.html.test.sigma()) //Ok(views.html.page("node","menu","0"))
  }

  def message = Action {
    import RegisterPicklers._
    RegisterPicklers.registerPicklers()
    val m = Message(User("Some User"),"message")
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }

  def iri = Action {
    import RegisterPicklers._
    RegisterPicklers.registerPicklers()
    val m = WebIRI("http://webintelligence.eu")
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }

  def item = Action {
    import RegisterPicklers._
    RegisterPicklers.registerPicklers()
    val m =  MenuItem(WebIRI("http://webintelligence.eu/pages/about"),"About")
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }

  def list = Action {
    import RegisterPicklers._
    RegisterPicklers.registerPicklers()
    val m =  List(WebIRI("http://webintelligence.eu/pages/about"),WebIRI("http://webintelligence.eu/pages/project"))
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }


  def strs = Action {
    import RegisterPicklers._
    RegisterPicklers.registerPicklers()
    val m =  List("one","two","three")
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }



  def menu = Action {


    RegisterPicklers.registerPicklers()
    val m = Menu(WebIRI("http://webintelligence.eu"),"Home", List(
      MenuItem(WebIRI("http://webintelligence.eu/pages/about"),"About"),
      MenuItem(WebIRI("http://webintelligence.eu/pages/project"),"Our projects")))
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }

//  def test = Action {
//    implicit request=>
//      request.body.asJson.map{case m=>
//        PicklerRegistry.unpickle(m) match {
//          case m:Message=>
//            val pickle = PicklerRegistry.pickle(m)
//            Ok(pickle)
//          case _ => BadRequest(Json.obj("status"->"KO","message"->"wrong message"))
//        }
//      }.getOrElse{BadRequest(Json.obj("status"->"KO","message"->"wrong message")) }
//
//  }




  def menu(root:String) =  UserAction {
    implicit request=>
      import Json._
      val mns = (1 to 5).map{case i=>
        Json.obj("uri"->s"http://webintelligence.eu/pages/menu$i", "label"->s"menu $i","page"->s"http://webintelligence.eu/pages/$i")
      }.toList
      val menu = Json.obj("menus"->Json.toJson(mns))
      Ok(menu).as("application/json")
  }


}
