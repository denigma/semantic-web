package controllers

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.playjson._
import models._
import org.scalax.semweb.rdf.{Lit, IRI}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sparql._
import models.Menu
import models.Menu
import models.User
import models.Message
import org.scalax.semweb.sparql.Pat
import models.MenuItem
import org.denigma.semantic.controllers.SimpleQueryController
import org.denigma.semantic.reading.selections._
import org.openrdf.model.{Literal, URI}
import scala.concurrent.Future
import org.denigma.semantic.sesame
import scala.util._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.collection.immutable._


/*
test controller
not working yet
 */
object Tests  extends Controller with SimpleQueryController{


  def sigma = Action {
    implicit request =>
      Ok(views.html.test.sigma()) //Ok(views.html.page("node","menu","0"))
  }

  def message = Action {
    RegisterPicklers.registerPicklers()
    val m = Message(User("Some User"),"message")
    val pickle = PicklerRegistry.pickle(m)
    Ok(pickle).as("application/json")
  }


  def menu =  UserAction.async{
    implicit request=>
      val dom = IRI(s"http://${request.domain}")
      val hasMenu = WI.PLATFORM / "has_menu" iri
      val hasItem = WI.PLATFORM / "has_item" iri
      val hasTitle = WI.PLATFORM / "has_title" iri

      val m = ?("menu")
      val item = ?("item")
      val tlt= ?("title")

      val selMenu = SELECT (item,tlt) WHERE {
        Pat( dom, hasMenu, m )
        Pat( m, hasItem, item)
        Pat( item, hasTitle, tlt)
      }

      val menuResult= this.select(selMenu).map(v=>v.map{case r=>
        Menu(dom / "menu",request.domain,r.toListMap.map{case list=>
         for{
            name<-list.get(item.name).collect{ case n:URI=>sesame.URI2IRI(n)}
            title<-list.get(tlt.name).collect{ case l:Literal=>sesame.literal2Lit(l)}

          } yield MenuItem(name,title.label)
        }.flatten)
      })

    menuResult.map[SimpleResult]{
      case Success(menu) => Ok(menu.children.toString())
      case Failure(th)=>BadRequest(th.toString)
    }

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




}
