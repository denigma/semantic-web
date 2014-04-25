package controllers

import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}
import play.api.libs.json.Json
import scala.util.{Failure, Success, Try}
import org.denigma.semantic.users.Accounts
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Controller responsible for everything with logins
 */
object Login extends PJaxPlatformWith("") with WithSyncWriter with SimpleQueryController with UpdateController
{
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


}
