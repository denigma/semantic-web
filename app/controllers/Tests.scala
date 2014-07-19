package controllers

import auth.UserAction
import org.denigma.binding.models._
import org.denigma.semantic.controllers.SimpleQueryController
import org.openrdf.model.{Literal, URI}
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.{WI, _}
import org.scalax.semweb.sesame._
import org.scalax.semweb.sparql.{Pat, _}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.util._


/*
test controller
not working yet
 */
object Tests  extends Controller with SimpleQueryController{


  def sigma = Action {
    implicit request =>
      Ok(views.html.test.sigma()) //Ok(views.html.page("node","menu","0"))
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
            name<-list.get(item.name).collect{ case n:URI=>URI2IRI(n)}
            title<-list.get(tlt.name).collect{ case l:Literal=>literal2Lit(l)}

          } yield MenuItem(name,title.label)
        }.flatten)
      })

    menuResult.map[Result]{
      case Success(menu) => Ok(menu.children.toString())
      case Failure(th)=>BadRequest(th.toString)
    }

  }

  def mailMe = Action {
    import com.typesafe.plugin._
    import play.api.Play.current
    val mail = use[MailerPlugin].email
    mail.setSubject("Testing how email works")
    mail.setRecipient("Antonkulaga <antonkulaga@gmail.com>","antonkulaga@gmail.com")
    mail.setFrom("Anton Kulaga <antonkulaga@gmail.com>")
    mail.send( "Some long text that is testing if everything is ok" )
    //sends both text and html
    Ok("Mail send")
  }

  def changeDomain(name:String) = UserAction {
    implicit request=>

    if(play.Play.isDev)
      if(name==request.domain)
        Ok(s"domain remains the same ${request.domain}")
      else
        Ok(s"domain change from ${request.domain} to $name").withSession("domain"->name)
    else
      BadRequest("App is not in dev mode!")
  }



}
