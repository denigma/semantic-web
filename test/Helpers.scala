import com.markatta.scalenium.Browser
import org.scalatest.selenium.Firefox
import play.api.test._
import org.openqa.selenium.WebDriver
import org.specs2.execute.{ AsResult, Result }
import play.api.test.WithBrowser
/**
 * Used to run specs within the context of a running server, and using a web browser
 *
 * @param webDriver The driver for the web browser to use
 * @param app The fake application
 * @param port The port to run the server on
 */
abstract class WithScalaBrowser[WEBDRIVER <: WebDriver](
                                                    val webDriver: WebDriver = WebDriverFactory(Helpers.FIREFOX),
                                                    val app: FakeApplication = FakeApplication(),
                                                    val port: Int = Helpers.testServerPort)
  extends scala.AnyRef with org.specs2.mutable.Around with org.specs2.specification.Scope
  //extends Around with Scope
{

  def this(
            webDriver: Class[WEBDRIVER],
            app: FakeApplication,
            port: Int) = this(WebDriverFactory(webDriver), app, port)

  implicit def implicitApp: FakeApplication = app
  implicit def implicitPort: Port = port

  lazy val browser = new TestBrowser(webDriver,"http://localhost:" + port)


  override def around[T: AsResult](t: => T): Result = {
    try {
      Helpers.running(TestServer(port, app))(AsResult.effectively(t))
    } finally {
      browser.quit()
    }
  }

  /*override def around[T: AsResult](t: => T): Result = {
    try {
      Helpers.running(TestServer(port, app))(AsResult.effectively(t))
    } finally {
      browser.quit()
    }
  }*/
}

case class TestBrowser(webDriver:WebDriver,baseURI:String="") extends Browser(webDriver){
  override def goTo(url: String): this.type = {
    require(url != "", "URL must not be empty")
    if(!url.contains(":")) driver.get(baseURI+url) else driver.get(url)
    this
  }
}