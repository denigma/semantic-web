import org.denigma.semantic.platform.AppConfig
import org.scalax.semweb.rdf.IRI
import play.api._
import play.api.mvc.{RequestHeader, WithFilters}
import play.filters.gzip.GzipFilter
import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.mvc.{WrappedRequest, Request}
import scaldi.Injector
import scaldi.play.ScaldiSupport
import scala.concurrent.Future
import akka.actor.ActorSystem
import spray.caching.{LruCache, Cache}
import spray.util._


/*
Global object that
 */
object Global extends WithFilters(new GzipFilter()) {

  override def onStart(app: Application) {
    Logger.info("Semantic-web application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Semantic-web application shutdown...")
  }


  override def onRouteRequest(request: RequestHeader): Option[Handler] = {

    //TODO: figure out what to do
    super.onRouteRequest(request)
  }

}

