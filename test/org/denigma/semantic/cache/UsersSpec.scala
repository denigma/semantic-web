package org.denigma.semantic.cache


import play.api.test.WithApplication

import scala.util.Try
import org.openrdf.query.BindingSet
import org.denigma.semantic.controllers.{SimpleQueryController, UpdateController}
import scala.concurrent.Future
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.controllers.sync.SyncSimpleController
import org.denigma.semantic.vocabulary._
import org.specs2.mutable.Specification
import org.denigma.semantic.model.IRI
import org.denigma.semantic.vocabulary.USERS
import org.denigma.semantic.model.Trip
import org.denigma.semantic.model.LitStr
import org.denigma.semantic.sparql._
import org.denigma.semantic.sparql.Pat
import org.denigma.semantic.actors.WatchProtocol.PatternResult


//class UsersSpec extends Specification {
//
//  /*
//  alias for "this"
//   */
//  self=>
//
//  class WithTestApp extends WithApplication with SimpleQueryController with UpdateController
//
//  val context = IRI(USERS.namespace)
//  val pasw1 = "password1"
//  val pasw2 = "password2"
//  val pasw3 = "password3"
//  val pasw4 = "password4"
//
//  val anton = USERS.user / "Anton"
//  val daniel = USERS.user / "Daniel"
//  val liz = USERS.user / "Liz"
//  val ilia = USERS.user / "Ilia"
//  val pr = USERS.props
//
//
//
//
//  "User accounts" should {
//
//
//
//
//    "register users" in new WithTestApp
//    {
//
//
//
//
//
//    }
//
//    "Authentificate users" in new WithTestApp
//    {
//
//
//    }
//
//
//  }
//}