package models
import scala.concurrent.Future
import org.openrdf.model.URI

/*
Model responsible for dealing with Users
 */
object Users {

  def byEmail(email:String):Future[User] = ???

  def auth(username:String,password:String) = ???

}


case class User(uri:URI,email:String,hash:String)
{
}