import scala.scalajs.js
import js.Dynamic.{ global => g }
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

@JSExport
object ScalaJavaScript {
  @JSExport
  def main(): Unit = {
    Changer("one","1. Hello World!").doAction()
    Changer("two","2. ScalaJS is speaking!").doAction()

  }

  /** Computes the square of an integer.
   *  This demonstrates unit testing.
   */
  def square(x: Int): Int = x*x
}

case class Changer(id:String,text:String) {

  def doAction() = {
    g.document.getElementById(id).textContent = text

  }
}