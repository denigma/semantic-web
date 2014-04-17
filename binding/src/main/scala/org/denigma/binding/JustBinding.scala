package org.denigma.binding

import org.scalajs.dom.{Event, HTMLElement}
import org.denigma.extensions._
import rx._
import org.scalajs.dom

/**
 * Just a basic subclass for all bindings
 */
class JustBinding {



  def makeId(el:HTMLElement,title:String) = {
    if (el.id.isNullOrUndef) {
      el.id = title + "_" + math.random
    }
    el.id
  }

  /*
  Binds value to rx
   */
  def bindRx[T](key:String,el:HTMLElement ,rx:Rx[T])(assign:(HTMLElement,T)=>Unit) = {
    val eid = this.makeId(el, key)
    lazy val obs: Obs = Obs(rx, eid, skipInitial = false) {
      dom.document.getElementById(eid) match {
        case null =>
          dom.console.info(s"$eid was not find, killing observable...")
          obs.kill()

        case element: HTMLElement =>
          val value = rx.now
          //el.dyn.obs = obs.asInstanceOf[js.Dynamic]
          assign(element, value)
      }
    }
    val o = obs //TO MAKE LAZY STUFF WORK
  }


  /**
   * Creates and even handler that can be attached to different listeners
   * @param el element
   * @param par rx parameter
   * @param assign function that assigns var values to some element properties
   * @tparam TEV type of event
   * @tparam TV type of rx
   * @return
   */
  def makeEventHandler[TEV<:Event,TV](el:HTMLElement,par:Rx[TV])(assign:(TEV,Var[TV],HTMLElement)=>Unit):(TEV)=>Unit = ev=> par match {
    case v:Var[TV] => assign(ev,v,el)
    case _=> dom.console.error(s"rx is not Var")
  }


}
