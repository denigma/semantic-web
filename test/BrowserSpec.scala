import org.denigma.semantic.platform.SP
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class BrowserSpec extends  PlaySpecification{
  //override def is = args(sequential = true) ^ super.is

  object FakeApp extends FakeApplication

  "conintain Evidence_Shape" in new WithScalaBrowser(webDriver = WebDriverFactory(FIREFOX), app = FakeApp) {
    SP.loadInitialData()
    browser.goTo("/test/shapes")
    val page = browser.pageSource
    SP.lg.debug(page)
    page.contains("Evidence_Shape") must beTrue
  }

}

