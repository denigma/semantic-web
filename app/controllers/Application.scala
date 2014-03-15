package controllers

import play.api.mvc._
import java.io.File
import play.api.libs.json.{JsError, JsObject, Json}
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.writing.DataWriter
import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.files.SemanticFileParser
import com.bigdata.counters.AbstractCounterSet
import org.denigma.semantic.users.Accounts
import scala.util._
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable._
import play.api.libs.json.{JsValue, Json}
import org.denigma.semantic._
import org.denigma.semantic.reading.selections.SelectResult
import org.denigma.semantic.controllers.QueryController
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import org.denigma.semantic.reading.QueryResultLike
import play.api.libs.json.JsObject
import play.api.libs.json.JsObject
import play.api.mvc.SimpleResult
import scala.util.Success
import scala.util.Failure
import play.api.libs.json.JsObject
import scala.concurrent.Future

/*
main application controller, responsible for index and some other core templates and requests
 */
object Application extends PJaxPlatformWith("") with WithSyncWriter with SemanticFileParser
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
       BadRequest(Json.obj("status" ->"KO","message"->"you are already logged in")).as("application/json")
      else
     {
       val au: Try[Unit] = if(username.contains("@")) Accounts.authByEmail(username,password) else Accounts.auth(username,password)
       au  match {
         case Success(_)=> Ok(Json.obj("status" ->"OK","message"->"login successful")).as("application/json").withSession("user" -> username)
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
        this.lg.error("some user decided to log out")
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


  def index(controller:String,action:String) = UserAction {
    implicit request=>
      Ok(views.html.index(controller,action,views.html.main()))

  }

  def menu(root:String) =  UserAction {
    implicit request=>
      import Json._
      val mns = (1 to 5).map{case i=>
        Json.obj("uri"->s"http://webintelligence.eu/pages/menu$i", "label"->s"menu $i","page"->s"http://webintelligence.eu/pages/$i")
      }.toList
      val menu = Json.obj("menus"->Json.toJson(mns))
      Ok(menu).as("application/json")
  }

  def page(uri:String) =  UserAction {
    implicit request=>

      Ok(views.html.pages.page("!!!!!!!!!!!!!!!!!!!!!"))
  }


  /*
   TODO: test upload code
   WARNING: NOT TESTED
    */
  def upload = UserAction(parse.multipartFormData) { implicit request =>
    import Json._
    val uploads = Play.getFile("public/uploads/")
    val p = uploads.getAbsolutePath
    val files: scala.List[JsObject] = request.body.files.map{ f=>
      val fname = f.filename

      val name = p+"/"+fname
      val file = new File(name)

      f.ref.moveTo(file,replace = true)

      val obj: JsObject = Json.obj(
        "name"->f.filename,
        "size"->file.length(),
        "url"-> (request.domain+"/uploads/"+f.filename)
      )
      val uplCon = "http://webintelligence.eu/uploaded/"
      val pr = this.parseFile(file,uplCon)

      if(pr.isFailure) obj + ("error"->toJson("WRONG SEMANTIC WEB FILE"))  else obj

    }.toList
    val res = Json.obj("files" -> Json.toJson(files))
    Ok(res)
  }
}
