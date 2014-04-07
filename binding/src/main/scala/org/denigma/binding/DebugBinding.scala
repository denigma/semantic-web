package org.denigma.binding

import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import org.scalajs.dom
import rx.core.Var

/**
 * TODO: fix
 * Has methods that show all state variables
 */
trait DebugBinding extends PropertyBinding{

  /**
   * Provides a nice way to debug
   */
  lazy val debug = Var(true)


  //TODO: rewrite
  def bindDebug(el:HTMLElement,ats:mutable.Map[String, dom.Attr]) = for {
    (key, value) <- ats
  }{
    key.toString match {

      case "debug" => this.showIf(el,value.value,el.style.display)

      case _ => //some other thing to do
    }
  }

  /**
   * Shows element if condition is satisfied
   * @param element Element that should be shown
   * @param disp
   */
  def addPanel(element:HTMLElement,deb: String,disp:String) = for ( b<-bools.get(deb) ) this.bindRx("debug",element,b) {
    case (el, true) => //TODO add debugging
    case (el, false) => //TODO remove debugging
  }

}
