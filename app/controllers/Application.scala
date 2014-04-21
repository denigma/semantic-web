package controllers
import models.RegisterPicklers._
import play.api.mvc._
import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.users.Accounts
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.templates.Html
import models.{RegisterPicklers, Menu, MenuItem}
import org.denigma.semantic.controllers.UpdateController

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sparql._
import org.scalax.semweb.sparql.Pat
import org.denigma.semantic.controllers.SimpleQueryController
import org.denigma.semantic.reading.selections._
import org.openrdf.model.{Literal, URI}
import scala.concurrent.Future
import scala.util._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.PicklerRegistry._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame

import org.scalax.semweb.sesame._


/*
main application controller, responsible for index and some other core templates and requests
 */
object Application extends PJaxPlatformWith("") with WithSyncWriter with SimpleQueryController with UpdateController
{
  def lifespan= UserAction {
    implicit request=>
      Ok(views.html.lifespan.lifespan())
  }

  /**
   * Provides login for the user
   * @param username username or email
   * @param password password
   * @return Failure or Success
   */
  def login(username:String,password:String) = UserAction{
    implicit request=>
      if(request.isSigned)
      {
       BadRequest(Json.obj("status" ->"KO","message"->"you are already logged in")).as("application/json")
      }
      else
     {
       val au: Try[Unit] = if(username.contains("@")) Accounts.authByEmail(username,password) else Accounts.auth(username,password)
       au  match {
         case Success(_)=> Ok(Json.obj("status" ->"OK","message"->s"logged in as $username")).as("application/json").withSession("user" -> username)
         case Failure(th) =>  Unauthorized(Json.obj("status" ->"KO", "message"->th.getMessage))
       }
     }
  }


  def tellBad(message:String) = BadRequest(Json.obj("status" ->"KO","message"->message)).as("application/json")

  def logout() = UserAction {
    implicit request=>
      if(request.isSigned)
        Ok(Json.obj("status"->"OK","message"->"loggedOut")).withNewSession
      else
      {
        this.lg.error("some user managed to log out being not logged in")
        BadRequest(Json.obj("status" ->"KO","message"->"you are not logged in!")).as("application/json")
      }

  }


  /**
   * Registers a user
   * @param username Username
   * @param email email
   * @param password password
   * @return Success[Boolean] or Failure
   */
  def register(username:String,email:String,password:String) = UserAction.async{
    implicit request=>
      if(request.isSigned)
      {
        this.lg.error(s"$username decided to register when logged in")
        Future.successful(BadRequest(Json.obj("status" ->"KO","message"->"You cannot register when you are already logged in!")).as("application/json"))
      }
      else
        {
        Accounts.register(username,email,password).map{
          case Success(true) =>
            Ok(Json.obj("status" ->"OK","message"->s"$username was successfuly registered")).withSession("user" -> username).as("application/json")
          case Success(false) =>
            lg.info(s"$username with $email was not cached")
            BadRequest(Json.obj("status" ->"KO","message"->"there is a user with same name/email")).as("application/json")
          case Failure(th) => BadRequest(Json.obj("status" ->"KO","message"->th.getMessage))

        }
      }
  }


  def index(): Action[AnyContent] =  UserAction {
    implicit request=>
      Ok(views.html.longevity.index(request))
  }


  /**
   * Renders menu for the website
   * @param domainName
   * @return
   */
  def menu(domainName:String = "") =  UserAction.async{
    implicit request=>
      val domain = if(domainName=="") request.domain else domainName
      val dom =  IRI(s"http://$domain")
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
        Menu(dom / "menu",domain,r.toListMap.map{case list=>
          for{
            name<-list.get(item.name).collect{ case n:URI=>sesame.URI2IRI(n)}
            title<-list.get(tlt.name).collect{ case l:Literal=>sesame.literal2Lit(l)}

          } yield MenuItem(name,title.label)
        }.flatten)
      })

      menuResult.map[SimpleResult]{
        case Success(res:Menu) =>
          RegisterPicklers.registerPicklers()
          val pickle: JsValue = PicklerRegistry.pickle(res)
          Ok(pickle).as("application/json")
        case Failure(th)=>BadRequest(th.toString)
      }

  }

  def page(uri:String) =  UserAction {
    implicit request=>


      val res: Html = views.html.longevity.index(request)
      Ok(res)
  }


}
