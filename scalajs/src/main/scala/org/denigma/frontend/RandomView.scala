package org.denigma.frontend

import org.denigma.frontend.views.OrdinaryView
import org.scalajs.dom.HTMLElement
import rx.core.Var
import scalatags.HtmlTag
import scalatags.all._
import scalatags.HtmlTag
import scala.util.Random
import rx.Rx
import scala.collection.immutable.Map
import org.scalajs.dom
import org.denigma.macroses


/**
 * For test purposes only
 */
class RandomView(el:HTMLElement) extends OrdinaryView("random",el){

  val segment: Var[HtmlTag] = Var{
    div(`class`:="ui segment",
      h1("This is title"),
      p("""value that changes: "START" """)
    )
  }


  val list = List("ONE","TWO","THREE","FOUR","SOME TEXT","THAT IS RANDOM")

  def update():Unit ={
    val value =  div(`class`:="ui segment",
      h1("This is title"),
      p(s"""value that changes: "${list(Random.nextInt(list.length))}" """)
    )
    segment() = value

  }



  dom.setInterval(update _, 100)



  /** Computes the square of an integer.
    *  This demonstrates unit testing.
    */
  def square(x: Int): Int = x*x

  lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)
}
