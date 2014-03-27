package org.denigma.frontend.bindings


import rx._
import org.denigma.macroses.js._
import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.scalajs.js
import scala.collection.immutable._



/**
 * Binds separate properties
 */
trait PropertyBinding{

  def bools:Map[String,Rx[Boolean]]
  def strings:Map[String,Rx[String]]
  //def doubles:Map[String,Rx[Double]]


  //  def extractBooleans[T]:Map[String,Rx[Boolean]] = macro Binder.booleanBindings_impl[T]
  //  def extractStrings[T]:Map[String,Rx[String]] = macro Binder.stringBindings_impl[T]
  //  def extractDoubles[T]:Map[String,Rx[Double]] = macro Binder.doubleBindings_impl[T]


  def extractAll[T: ClassToMap](t: T) =  implicitly[ClassToMap[T]].asMap(t)
  def extractStringRx[T: StringRxMap](t: T) =  implicitly[StringRxMap[T]].asStringRxMap(t)
  def extractListRx[T: ListRxMap](t: T) =  implicitly[ListRxMap[T]].asListRxMap(t)
  def extractBooleanRx[T: BooleanRxMap](t: T) =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)


  def bindProperties(el:HTMLElement,ats:mutable.Map[String, js.String]) = {
    this.showIf(el,ats)
    this.hideIf(el,ats)
  }

  def showIf(el:HTMLElement,ats:mutable.Map[String, js.String]) =
    for {
      show<-ats.get("showif")
      b<-bools.get(show)
    }
    {
      val disp: String = el.style.display
      val rShow: Rx[String] = Rx{ if(b()) disp else "none"}
      el.style.display = rShow()
    }

  def hideIf(el:HTMLElement,ats:mutable.Map[String, js.String]) =
    for {
      hide<-ats.get("hideif")
      b<-bools.get(hide)
    }
    {
      val disp: String = el.style.display
      val rHide: Rx[String] = Rx{ if(!b()) disp else "none"}
      el.style.display = rHide()
    }



}

