package auth

import play.api.mvc.{Result, Request, Action}
import scala.concurrent.Future
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.USERS

/**
 * Auth Action wrapper
 * @param action
 * @tparam A
 */
case class WithUser[A](action: Action[A]) extends Action[A] {



  def apply(request: Request[A]): Future[Result] = {
    val user = request.session.get("user").map(name=>if(name.contains(":")) IRI(name) else IRI(USERS.user / "name"))
    val req = AuthRequest(user,request)
    action(req)
  }

  lazy val parser = action.parser
}
