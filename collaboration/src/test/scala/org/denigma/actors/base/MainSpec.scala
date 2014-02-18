package org.denigma.actors.base


import akka.pattern.ask


import akka.testkit._

import akka.actor._
import org.denigma.actors.messages._
import scala.util.Success
import  scala.language.postfixOps
import org.denigma.actors.staff.EventActor
import scala.reflect.ClassTag
import scala.concurrent.Await
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.json.{Json, JsValue}
//import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.{ ExecutionContext, Promise }
import akka.dispatch._

/**
 * Main features of MainTestSpec
 * @param _system
 */
class MainSpec[T<:EventActor:ClassTag](_system:ActorSystem) extends BasicSpec(_system){


  val master = TestActorRef[T]
  val masterActor= master.underlyingActor



  def addUser(username:String, password:String)
  {
    this.addUser(username,"noemail@gmail.com","notoken",password)
  }


  /**
   * Add user to master actors
   * @param username          name of the user
   * @param email email of the user
   * @param token token of the user
   * @param hash user password hash
   */
  def addUser(username:String, email:String, token:String, hash:String)
  {

    //val Success(ConnectedSimple(input,output)) = ( master ? SimpleJoin(user,password) ).mapTo[ConnectedSimple].value.get
    val fut = (master ? Join(username,email,token,hash)).mapTo[Connected]
    val con = Await.result[Connected](fut,this.timeout.duration)
    channels = channels :+ this.extractChannels(username,con)(this.masterActor.now)


  }

}
