import play.api._

/*
Global object that
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Semantic-web application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Semantic-web application shutdown...")
  }

}