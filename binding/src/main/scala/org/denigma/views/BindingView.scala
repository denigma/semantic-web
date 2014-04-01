package org.denigma.views

import scala.collection.immutable.Map
import org.scalajs.dom.{Attr, HTMLElement}
import org.scalajs.dom.extensions._
import scala.collection.mutable
import org.scalajs.dom
import org.denigma.extensions
import extensions._

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.denigma.binding.JustBinding
import scala.util.{Success, Failure}

object BindingView {
  /**
   * created if we do not know the view at all
   * @param name of the view
   * @param elem dom element inside
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
 * @param name of te view
 * @param elem element inside
 */
abstract class BindingView(val name:String,elem:dom.HTMLElement) extends JustBinding
{

  /**
   * Id of this view
   */
  val id: String =this.makeId(elem,this.name)

  var subviews = Map.empty[String,BindingView]


  def addView(view:BindingView) = this.subviews = this.subviews + (view.id -> view)


  /**
   * Extracts view by name from element
   * @param viewName name of the view
   * @param el html element
   * @param params some other optional params needed to init the view
   * @return
   */
  def inject(viewName:String,el:HTMLElement,params:Map[String,Any]): BindingView ={factories.get(viewName) match {
    case Some(fun)=>
      fun(el,params) match {
        case Success(view)=>view

        case Failure(e)=>
          //dom.console.error(e.toString)
          if(e!=null)
            dom.console.error(s"cannot initialize the view for $viewName because of ${e.toString}")
          else
            dom.console.error(s"Cannot initialize the view for $viewName")
          BindingView.apply(name,el)
    }
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
          this.subviews.getOrElse(el.id,
          {
            val params = el.attributes.collect{case (key,value) if key.contains("data-param-")=> key.replace("data-param-", "") -> value.value.asInstanceOf[Any]}.toMap
            val v = this.inject(view.value,el,params)
            v.bind(el)
            this.addView(v) //the order is intentional
            v
          } )

        case _=>
          this.bindElement(el)
          if(el.hasChildNodes()) el.childNodes.foreach {
            case el: HTMLElement => this.bind(el)
            case _ => //skip
          }
      }

}

