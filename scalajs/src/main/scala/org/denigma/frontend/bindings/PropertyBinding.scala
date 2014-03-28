package org.denigma.frontend.bindings


import rx._
import org.denigma.macroses.js._
import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.scalajs.js
import scala.collection.immutable._
import org.scalajs.dom
import org.denigma.frontend.extensions._

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
  def extractBooleanRx[T: BooleanRxMap](t: T) =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)


  def bindProperties(el:HTMLElement,ats:mutable.Map[String, dom.Attr]) = for {
    (key, value) <- ats
  }{
    key match {

      case "showif" => this.showIf(el,value.value)
      case "hideif" => this.hideIf(el,value.value)
      case "bind" => this.bindProperty(el,key,value)
      case _ => //some other thing to do
  }
  }

  def showIf(el:HTMLElement,show: String) = for ( b<-bools.get(show) )
    {
      val disp: String = el.style.display
      val rShow: Rx[String] = Rx{ if(b()) disp else "none"}
      el.style.display = rShow()
    }

  def hideIf(el:HTMLElement,hide: String) = for ( b<-bools.get(hide) )
    {
      val disp: String = el.style.display
      val rHide: Rx[String] = Rx{ if(!b()) disp else "none"}
      el.style.display = rHide()
    }


  /**
   * Binds property value to attribute
   * @param el Element
   * @param key
   * @param att
   */
  def bindProperty(el:HTMLElement,key:String,att:dom.Attr) = (key,el.tagName.toLowerCase) match {
    //case ("bind","input")=>

    case ("bind",other)=> el.textContent = att.value
    case _=> dom.console.info(s"unknown binding")


  }


  def attrib(key:String,el:HTMLElement) = if(key=="bind") {
    if(el.tagName.toString.toLowerCase.contains("input")){

    }
  }




}

