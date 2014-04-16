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

  def mailMe = Action {
    import play.api.Play.current
    import com.typesafe.plugin._
    val mail = use[MailerPlugin].email
    mail.setSubject("Testing how email works")
    mail.setRecipient("Antonkulaga <antonkulaga@gmail.com>","antonkulaga@gmail.com")
    mail.setFrom("Anton Kulaga <antonkulaga@gmail.com>")
    mail.send( "Some long text that is testing if everything is ok" )
    //sends both text and html
    Ok("Mail send")
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
