package org.denigma.semantic.cache


import org.specs2.mutable._
import play.api.test.WithApplication

import scala.util.Try
import org.openrdf.query.BindingSet
import org.denigma.semantic.controllers.{WithLogger, SimpleQueryController, UpdateController}
import scala.concurrent.Future
import org.denigma.semantic.reading.selections._

import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.users.Accounts
import org.denigma.rdf.model.vocabulary._
import org.denigma.rdf.sparql._

import org.denigma.rdf.model.{StringLiteral, Trip, IRI}
import org.denigma.rdf.model.vocabulary.USERS


class CacheSpec extends Specification {

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



  val basic: InsertQuery = InsertQuery {
    INSERT (
      DATA (
        GRAPH(context,
          Trip(
            anton iri,
            pr hasPasswordHash,
            StringLiteral(pasw1)
          ),
          Trip(
            anton iri,
            pr hasEmail,
            StringLiteral("anton@gmail.com")
          ),
          Trip(
            daniel iri,
            pr hasPasswordHash,
            StringLiteral(pasw2)
          ),
          Trip(
            daniel iri,
            pr hasEmail,
            StringLiteral("daniel@gmail.com")
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
            StringLiteral(pasw3)
          ),
          Trip(
            USERS.user / liz iri,
            USERS.props hasEmail,
            StringLiteral("liz@gmail.com")
          ),
          Trip(
            USERS.user / ilia iri,
            USERS.props hasPasswordHash,
            StringLiteral(pasw4)
          ),
          Trip(
            USERS.user / ilia iri,
            USERS.props hasEmail,
            StringLiteral("ilia@gmail.com")
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
      h.getBinding("p").getValue.stringValue().contains("anton@gmail.com") should beTrue

      //Users.mails.size shouldEqual 2

      val patres: Future[Try[PatternResult]] = Accounts.fill()
      val tu: Try[PatternResult] = this.awaitRead(patres)
      tu.isSuccess should beTrue
      val utr = tu.get

      utr.name shouldEqual Accounts.cacheName
      utr.results.size shouldEqual 2
      //lg.debug(utr.results.toString())
      utr.results(Accounts.hasEmail).exists(p=>p.o.stringValue.contains("anton@gmail.com")) should beTrue
      utr.results(Accounts.hasEmail).exists(p=>p.o.stringValue.contains("daniel@gmail.com")) should beTrue


    }

    "Populate cache object" in new WithTestApp
    {
      this.awaitRead(Accounts.lastActivation)
      Accounts.lastActivation.isCompleted should beTrue


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
      Accounts.active should beTrue

      //this.lg.error(Accounts.mails.toList.toString()+"\n")

//      Accounts.mails.size shouldEqual 2
//      Accounts.mails.contains(anton iri) should beTrue
//      Accounts.hashes.contains(daniel iri) should beTrue


    }


  }
}