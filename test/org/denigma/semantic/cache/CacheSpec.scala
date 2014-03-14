package org.denigma.semantic.cache

import org.specs2.mutable.Specification
import org.denigma.semantic.test.LoveHater
import org.denigma.semantic.controllers.SimpleQueryController

import org.specs2.mutable._
import play.api.test.WithApplication

import org.denigma.semantic.test.LoveHater
import scala.util.Try
import org.openrdf.query.{BindingSet, TupleQueryResult}
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.controllers.{SimpleQueryController, UpdateController}
import scala.concurrent.Future
import play.api.libs.concurrent.Akka
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql._
import org.denigma.semantic.sparql
import scala.collection.JavaConversions._
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading._
import org.denigma.semantic.controllers.sync.{SyncSimpleController, SyncUpdateController}
import org.denigma.semantic.vocabulary._
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql.InsertQuery
import org.denigma.semantic.model._
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.users.Accounts



class CacheSpec extends Specification {

  /*
  alias for "this"
   */
  self=>

  class WithTestApp extends WithApplication with SimpleQueryController with UpdateController

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



  val basic: InsertQuery = InsertQuery {
    INSERT (
      DATA (
        GRAPH(context,
          Trip(
            anton iri,
            pr hasPasswordHash,
            LitStr(pasw1)
          ),
          Trip(
            anton iri,
            pr hasEmail,
            LitStr("anton@gmail.com")
          ),
          Trip(
            daniel iri,
            pr hasPasswordHash,
            LitStr(pasw2)
          ),
          Trip(
            daniel iri,
            pr hasEmail,
            LitStr("daniel@gmail.com")
          )

        )
      )
    )
  }

  val second: InsertQuery = InsertQuery {
    INSERT (
      DATA (
        GRAPH(context,
          Trip(
            USERS.user / liz iri,
            USERS.props hasPasswordHash,
            LitStr(pasw3)
          ),
          Trip(
            USERS.user / liz iri,
            USERS.props hasEmail,
            LitStr("liz@gmail.com")
          ),
          Trip(
            USERS.user / ilia iri,
            USERS.props hasPasswordHash,
            LitStr(pasw4)
          ),
          Trip(
            USERS.user / ilia iri,
            USERS.props hasEmail,
            LitStr("ilia@gmail.com")
          )
        )
      )
    )
  }




  "Cache mechanism" should {




    "Extract initial data from the database" in new WithTestApp
    {


      val upq = basic.insert.stringValue


      val u = this.update(upq)
      val ru = this.awaitWrite(u)
      ru.isSuccess should beTrue

      val q = SELECT( ?("p") ) WHERE {
        Pat(
          anton iri, pr hasEmail, ?("p")
        )
      }

      val r = this.awaitRead(this.select(q))
      r.isSuccess should beTrue
      val rl = r.get.toList
      rl.size shouldEqual 1
      val h: BindingSet = rl.head
      h.getBinding("p").getValue.stringValue() shouldEqual "anton@gmail.com"

      //Users.mails.size shouldEqual 2

      val patres: Future[Try[PatternResult]] = Accounts.fill()
      val tu: Try[PatternResult] = this.awaitRead(patres)
      tu.isSuccess should beTrue
      val utr = tu.get

      utr.name shouldEqual Accounts.name
      utr.results.size shouldEqual 2
      utr.results(Accounts.hasEmail).exists(p=>p.getObject.stringValue()=="anton@gmail.com") should beTrue
      utr.results(Accounts.hasEmail).exists(p=>p.getObject.stringValue()=="daniel@gmail.com") should beTrue


    }

    "Populate cache object" in new WithTestApp
    {


      val upq = basic.insert.stringValue

      ChangeManager.consumers.contains(Accounts) should beTrue


      val u = this.update(upq)
      val ru = this.awaitWrite(u)
      ru.isSuccess should beTrue

      val q = SELECT( ?("p") ) WHERE {
        Pat(
          anton iri, pr hasEmail, ?("p")
        )
      }

      val r = this.awaitRead(this.select(q))
      r.isSuccess should beTrue
      val rl = r.get.toList
      rl.size shouldEqual 1

      Accounts.mails.size shouldEqual 2
      Accounts.mails.contains(anton iri) should beTrue
      Accounts.hashes.contains(daniel iri) should beTrue


    }


  }
}