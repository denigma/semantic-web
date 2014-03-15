import play.api._
import play.api.mvc.WithFilters
import play.filters.gzip.GzipFilter
/*
Global object that
 */
object Global extends WithFilters(new GzipFilter())  {

  override def onStart(app: Application) {
    Logger.info("Semantic-web application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Semantic-web application shutdown...")
  }

}