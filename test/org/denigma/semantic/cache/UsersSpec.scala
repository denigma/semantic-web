package org.denigma.semantic.cache


import play.api.test.WithApplication

import org.denigma.semantic.controllers.{WithLogger, SimpleQueryController, UpdateController}
import org.specs2.mutable.Specification
import org.denigma.semantic.model.IRI
import org.denigma.semantic.vocabulary.USERS
import org.denigma.semantic.users.{Account, Accounts}
import scala.util.Try


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
      this.awaitRead{ Accounts.register("nick","nick@gmail.com","adm") } should throwA[Accounts.PasswordTooShortException]

      Accounts.awaitRead{
        Accounts.register("nick","nickogmail.com","admin")
      }  should throwA[Accounts.EmailNotValidException]


      Accounts.userByEmail("nick@gmail.com").isDefined should  beFalse

      val reg1 = Accounts.awaitRead{
        Accounts.register("nick","nick@gmail.com","admin")
      }
      reg1.isSuccess should beTrue
      reg1.get should beTrue

      Thread.sleep(100)

      val uso: Option[Account] = Accounts.userByEmail("nick@gmail.com")
      uso.isDefined should beTrue
      uso.get.name.stringValue().contains("nick") should beTrue
      uso.get.email shouldEqual "nick@gmail.com"
      uso.get.hash shouldNotEqual "admin"

      Accounts.auth("anton","password").isSuccess should beFalse
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
      uso2.get.name.stringValue().contains("anton") should beTrue
      uso2.get.email shouldEqual "antonkulaga@gmail.com"
      uso2.get.hash shouldNotEqual "password"

      Accounts.auth("anton","password").isSuccess should beTrue
      Accounts.auth("nick","wrongpassword").isSuccess should beFalse
      Accounts.auth("nick","admin").isSuccess should beTrue

      Accounts.authByEmail("antonkulaga@gmail.com","admin").isSuccess should beFalse
      Accounts.authByEmail("nick@gmail.com","admin").isSuccess should beTrue
      Accounts.authByEmail("antonkulaga@gmail.com","password").isSuccess should beTrue



    }




  }
}