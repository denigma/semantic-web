import org.denigma.semantic.platform.AppConfig
import play.api._
import play.api.mvc.{RequestHeader, WithFilters}
import play.filters.gzip.GzipFilter
import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import org.denigma.semantic.model.IRI
import play.api.mvc.{WrappedRequest, Request}
import scaldi.Injector
import scaldi.play.ScaldiSupport

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

