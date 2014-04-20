package org.denigma.semantic.cache



import org.denigma.semantic.controllers.{WithLogger, SimpleQueryController, UpdateController}
import org.specs2.mutable.Specification
import org.specs2.execute

import org.denigma.semantic.users.Accounts
import scala.util.{Failure, Try}
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.test._
import org.denigma.semantic.reading.selections._

import org.denigma.semantic.users.Account
import scala.util.Failure
import scala.Some
import org.denigma.semantic.users.Account
import scala.util.Success
import org.denigma.rdf.sparql.Pat

import org.denigma.rdf.model.{AnyLit, IRI}
import org.denigma.rdf.model.vocabulary._

class UsersSpec extends Specification {

  /*
  alias for "this"
   */
  self=>

  class WithTestApp extends WithApplication with SimpleQueryController with UpdateController with WithLogger

  val context = IRI(USERS.namespace)
  val pasw1 = "password1"
  val pasw2 = "password2"
  val pasw3 = "password3"
  val pasw4 = "password4"

  val anton = USERS.user / "Anton"
  val daniel = USERS.user / "Daniel"
  val liz = USERS.user / "Liz"
  val ilia = USERS.user / "Ilia"
  val pr = USERS.props




  "User accounts" should {

    "register users" in new WithTestApp
    {

      this.awaitRead(Accounts.lastActivation)
      this.awaitRead{ Accounts.register("nick","nick@gmail.com","adm") } should beFailedTry[Boolean] //throwA[Accounts.PasswordTooShortException]

      Accounts.awaitRead{
        Accounts.register("nick","nickogmail.com","admin")
      }  should beFailedTry[Boolean]//throwA[Accounts.EmailNotValidException]


      Accounts.userByEmail("nick@gmail.com").isDefined should  beFalse

      val reg1 = Accounts.awaitRead{
        Accounts.register("nick","nick@gmail.com","admin")
      }
      reg1.isSuccess should beTrue
      reg1.get should beTrue

      Thread.sleep(100)

      val uso: Option[Account] = Accounts.userByEmail("nick@gmail.com")
      uso.isDefined should beTrue
      uso.get.name.stringValue.contains("nick") should beTrue
      uso.get.email shouldEqual "nick@gmail.com"
      uso.get.hash shouldNotEqual "admin"

      val auf = Accounts.auth("anton","password")
      auf.isSuccess should beFalse
      auf match {
        case Success(some)=> execute.Failure("should be failure")
        case Failure(f)=> f.getMessage.contains("empty iterator") must beFalse


      }
      Accounts.auth("nick","wrongpassword").isSuccess should beFalse
      Accounts.auth("nick","admin").isSuccess should beTrue

      Accounts.authByEmail("antonkulaga@gmail.com","admin").isSuccess should beFalse
      Accounts.authByEmail("nick@gmail.com","wrongpassword").isSuccess should beFalse
      Accounts.authByEmail("nick@gmail.com","admin").isSuccess should beTrue

      val reg2: Try[Boolean] = Accounts.awaitRead{
        Accounts.register("anton","antonkulaga@gmail.com","password")
      }
      reg2.isSuccess should beTrue
      reg2.get should beTrue

      Thread.sleep(100)


      val uso2: Option[Account] = Accounts.userByName("anton")
      uso2.isDefined should beTrue
      uso2.get.name.stringValue.contains("anton") should beTrue
      uso2.get.email shouldEqual "antonkulaga@gmail.com"
      uso2.get.hash shouldNotEqual "password"

      Accounts.auth("anton","password").isSuccess should beTrue
      Accounts.auth("nick","wrongpassword").isSuccess should beFalse
      Accounts.auth("nick","admin").isSuccess should beTrue

      Accounts.authByEmail("antonkulaga@gmail.com","admin").isSuccess should beFalse
      Accounts.authByEmail("nick@gmail.com","admin").isSuccess should beTrue
      Accounts.authByEmail("antonkulaga@gmail.com","password").isSuccess should beTrue



    }


    "Provide REST right" in new WithTestApp {

      val Some(result1) = route(FakeRequest(GET, "/users/login?username=anton&password=wrong"))
      status(result1)(defaultAwaitTimeout) shouldEqual(UNAUTHORIZED)
      session(result1)(defaultAwaitTimeout).get("user").isDefined should beFalse


      val Some(result2) = route(FakeRequest(GET, "/users/register?username=antonkulaga&email=anton@email.com&password=rightpassword"))
      status(result2)(defaultAwaitTimeout) shouldEqual(OK)
      val ou = session(result2)(defaultAwaitTimeout).get("user")
      ou.isDefined should beTrue
      ou.get.contains("antonkulaga") should beTrue


      val Some(result3) = route(FakeRequest(GET, "/users/register?username=antonkulaga&email=anton@email.compassword=otherpassword"))
      status(result3)(defaultAwaitTimeout) shouldEqual(BAD_REQUEST)

//      val Some(result4) = route(FakeRequest(GET, "/users/login?username=antonkulaga&password=rightpassword"))
//      session(result4)(defaultAwaitTimeout).get("user").isDefined should beTrue
//      status(result4)(defaultAwaitTimeout) shouldEqual(BAD_REQUEST)

//      val Some(result5) = route(FakeRequest(GET, "/users/logout"))
//      status(result5)(defaultAwaitTimeout) shouldEqual(OK)
//      session(result5)(defaultAwaitTimeout).get("user").isDefined should beFalse
//
//      val Some(result6) = route(FakeRequest(GET, "/users/login?username=anton@email.com&password=wrongpassword"))
//      session(result6)(defaultAwaitTimeout).get("user").isDefined should beFalse
//      status(result6)(defaultAwaitTimeout) shouldEqual(BAD_REQUEST)

//      val Some(result7) = route(FakeRequest(GET, "/users/login?username=anton@email.com&password=rightpassword"))
//      status(result7)(defaultAwaitTimeout) shouldEqual(OK)
//      session(result7)(defaultAwaitTimeout).get("user").isDefined should beTrue



    }




  }
}