package org.denigma.binding

import scala.collection.immutable.Map
import rx._
import org.scalajs.dom.HTMLElement
import org.scalajs.dom
import org.denigma.extensions._
/**
 * Does binding for classes
 */
trait ClassBinder {
  self:JustBinding=>
  def strings:Map[String,Rx[String]]
  def bools:Map[String,Rx[Boolean]]

  /**
   * Shows only if condition is true
   * @param element html element
   * @param className name of the class that will be added if conditional is true
   * @param cond conditional rx
   */
  def classIf(element:HTMLElement,className: String, cond:String) = for ( b<-bools.getOrError(cond) ) this.bindRx(className,element,b){
    case (el,cl) if el.classList.contains(className)=>
      if(!cl) el.classList.remove(className)
    case (el,cl) =>
      if(cl) el.classList.add(className)
  }

  def classUnless(element:HTMLElement,className: String, cond:String) = for ( b<-bools.getOrError(cond) ) this.bindRx(className,element,b){
    case (el,cl) if el.classList.contains(className)=>if(cl) el.classList.remove(className)
    case (el,cl) =>if(!cl) el.classList.add(className)
  }

  def bindClass(element:HTMLElement,prop: String) = for ( str<-strings.get(prop) ) this.bindRx(prop,element,str.zip){
    case (el,(oldVal,newVal)) =>
      if(el.classList.contains(oldVal))el.classList.remove(oldVal)
      el.classList.add(newVal)
    case _ => dom.console.error(s"error in bindclass for ${prop}")
  }
}
