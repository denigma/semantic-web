package org.denigma.frontend.views

import scala.collection.immutable.Map
import rx._
import scalatags.HtmlTag
import org.scalajs.dom
import org.scalajs.dom.{Attr, HTMLElement, Node}
import org.scalajs.dom.extensions._
import org.denigma.frontend.extensions._
import org.denigma.frontend.views._
import org.denigma.macroses.js.Binder
import scala.collection.mutable
import scala.scalajs.js
import org.denigma.frontend.views._
import org.scalajs.dom

import js.Dynamic.{ global => g }

object BindingView {
  /**
   * created if we do not know the view at all
   * @param name
   * @param elem
   */
  class JustView(name:String,elem:dom.HTMLElement) extends BindingView(name,elem){
    override def bindAttributes(el: HTMLElement, ats: mutable.Map[String, dom.Attr]): Unit = {
      //does nothing
    }
  }

  def apply(name:String,elem:dom.HTMLElement) = new JustView(name,elem)
}

/**
 * Basic view for binding
 * @param name
 * @param elem
 */
abstract class BindingView(val name:String,elem:dom.HTMLElement)
{
  /**
   * Id of this view
   */
  val id: String ={

    if(elem.id === null)
    {
      elem.id = name+"_"+math.random
    }
    elem.id
  }

  var subviews = Map.empty[String,BindingView]


  def addView(view:BindingView) = this.subviews = this.subviews + (view.id -> view)


  def inject(viewName:String,el:HTMLElement,other:AnyRef*): BindingView ={factories.get(viewName) match {
    case Some(fun)=>fun(el,other)
    case _ =>
      dom.console.error(s"cannot find view class for $viewName")
      BindingView.apply(name,el)
  }
  }

  protected var _element = elem
  def element = _element
  def element_=(value:HTMLElement): Unit = if(_element!=value) {
    this._element = value
    this.bind(value)
  }

  def bindAttributes(el:HTMLElement,ats:mutable.Map[String, Attr] )

  /**
   * Binds element
   * @param el
   */
  def bindElement(el:HTMLElement) = {
    val ats: mutable.Map[String, Attr] = el.attributes.collect{
      case (key,attr) if key.contains("data-") && !key.contains("data-view") => (key.replace("data-",""),attr)
    }
    this.bindAttributes(el,ats)

  }
//
  /**
   * Binds nodes to the element
   * @param el
   */
  def bind(el:HTMLElement):Unit =   el.attributes.get("data-view") match {

        case Some(view) if el.id.toString!=this.id =>
          this.subviews.getOrElse(el.id,   {
            val v = this.inject(view.value,el)
            v.bind(el)
            this.addView(v) //the order is intentional
            v   } )

        case _=>
          this.bindElement(el)
          if(el.hasChildNodes()) el.childNodes.foreach {
            case el: HTMLElement => this.bind(el)
            case _ => //skip
          }
      }

}

