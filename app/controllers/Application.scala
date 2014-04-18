package controllers

import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerMaterializersImpl._
import org.scalajs.spickling.playjson._
import models.RegisterPicklers._
import play.api.mvc._
import java.io.File
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.files.SemanticFileParser
import org.denigma.semantic.users.Accounts
import scala.util._
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import play.api.templates.Html
import org.scalajs.spickling.PicklerRegistry
import models.{RegisterPicklers, MenuItem, Menu}
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController, QueryController}
import org.denigma.rdf.IRI

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


  def menu(root:String) =  UserAction {
    implicit request=>
      val testMenu: Menu = Menu(IRI("http://longevity.org.ua/menu"),"Longevity.org.ua", List(
        MenuItem(IRI("http://longevity.org.ua/pages/manifesto"),"Долголетие Украины"),
        MenuItem(IRI("http://longevity.org.ua/pages/research"),"Исследования"),
        MenuItem(IRI("http://longevity.org.ua/pages/events"),"Активизм"),
        MenuItem(IRI("http://longevity.org.ua/pages/members"),"Участники"),
        MenuItem(IRI("http://longevity.org.ua/pages/projects"),"Проекты"),
        MenuItem(IRI("http://longevity.org.ua/pages/projects"),"Участвовать"),
        MenuItem(IRI("http://longevity.org.ua/pages/projects"),"Контакты")
      ))

      RegisterPicklers.registerPicklers()

      val pickle: JsValue = PicklerRegistry.pickle(testMenu)
      Ok(pickle).as("application/json")
  }

  def page(uri:String) =  UserAction {
    implicit request=>


      val res: Html = views.html.longevity.index(request)
      Ok(res)
  }


}
