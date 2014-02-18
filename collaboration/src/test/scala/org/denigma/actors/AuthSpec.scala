package org.denigma.actors

import org.denigma.actors.base.MainSpec
import org.denigma.actors.mock.MockMaster


import scala.concurrent.duration._
import akka.pattern.ask
import akka.actor._
import  scala.language.postfixOps
import org.denigma.actors.models._
import scala.concurrent.Await
import org.denigma.actors.models.UserNew
import org.denigma.actors.messages.Join
import org.denigma.actors.models.UserMerge
/**
 * Test to check if Auth works well
 * @param _system
 */
class AuthSpec(_system:ActorSystem) extends MainSpec[MockMaster](_system)// extends BasicSpec(_system)
{
  def this() = this(ActorSystem("MySpec"))

  val auth = this.masterActor.authWorker
  val authActor = this.masterActor.authWorker

  def join(username:String,email:String,token:String,hash:String):UserStatusLike = {
    val fut = (this.authActor ? Join(username,email,token,hash)).mapTo[UserStatusLike]
    Await.result(fut,timeout.duration)
  }


  "Auth worker" should {

    "create new User" in {

      val rNew = this.join(user1,"some@gmail.com","token1",password1)

      rNew match
      {
          case value:UserNew=> print("rnew right")
          case value:UserStatusLike=> this.fail("wrong user type")
          case _ => this.fail("wrong result")
      }

      this.join(user2,"some@gmail.com","token1",password2)
    }

    "prohibit to add User with wrong password" in {
      val rNone = this.join(user1,"some@gmail.com","token1","wrong password")
      rNone match
      {
          case value:UserNew=> this.fail("do not allow to add user")
          case value:UserMerge=> this.fail("do not allow to merge user")
          case value:UserProhibit=> println("right userlike")
          case value:UserStatusLike=> this.fail("wrong user type")
          case _ => this.fail("wrong result")
      }


    }


    "merge user with right password" in {
      val rMerge = this.join(user1,"some@gmail.com","token1",password1)
      rMerge match
      {
          case value:UserNew=> this.fail("do not allow to add user")
          case value:UserMerge=> println("right userlike")
          case value:UserProhibit=> this.fail("wrong prohibion for user")
          case value:UserStatusLike=> this.fail("wrong user type")
          case _ => this.fail("wrong result")

      }


    }
  }

  "Master actor with auth" should {
        "respond" in {
          val msg = Join(user1, "some@gmail.com" , "token", password1)
          master ! msg
          //this.auth ! msg
          this.receiveOne(2 seconds)

        }

  }



}
