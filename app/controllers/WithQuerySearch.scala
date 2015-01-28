package controllers
import org.denigma.semantic.controllers.sync.WithSyncWriter
import play.api.libs.json.JsValue
import play.twirl.api.Html
import org.denigma.semantic.controllers.{ShapeController, UpdateController, SimpleQueryController}

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{Res, RDFValue, IRI}
import org.scalax.semweb.rdf.vocabulary.WI
import org.openrdf.model.{Value, Literal, URI}
import views.html
import views.html.index
import scala.util._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame
import org.scalax.semweb.sparql._
import scala.concurrent.duration._
import spray.caching.{LruCache, Cache}
import scala.concurrent.{ExecutionContext, Future}
import auth.{AuthRequest, UserAction}
import ExecutionContext.Implicits.global
import org.scalax.semweb.sesame._


trait WithQuerySearch {
  self:ShapeController=>


  val selectCache:Cache[String] = LruCache(timeToIdle = 5 minutes)

  def queryFor(q:Res): Future[String] = {
    import org.scalax.semweb.sparql._
    val t = ?("text")
    val hasText = IRI(WI.PLATFORM / "hasText")
    val query = SELECT(t) WHERE Pat(q, hasText, t)
    play.api.Logger.info("QUERY = " +query.stringValue)
    selectCache(q.stringValue) {
      this.select(query.stringValue).flatMap {
        case Success(res) =>
          res.toListMap.headOption match {
            case None =>
              play.api.Logger.debug("SHAPE RES " + res.toString)
              Future.failed[String](new Error(s"no query or shape"))
            case Some(r) if !r.contains("text") => Future.failed[String](new Error(s"no query or shape"))
            case Some(r) if r.contains("text") => Future.successful(r("text").stringValue)
          }
      }
    }
  }

}
