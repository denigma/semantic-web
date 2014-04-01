package org.denigma.views

import org.scalajs.dom.{Attr, TextEvent, MouseEvent, HTMLElement}
import scala.collection.mutable
import scala.scalajs.js
import org.scalajs.dom
import org.denigma.views.OrdinaryView
import rx.{Var, Rx}
import scalatags.HtmlTag
import dom.extensions._
import org.denigma.binding.CollectionBinding
import scala.collection.immutable.Map


abstract class ListView(name:String,element:HTMLElement, params:Map[String,Any]) extends BindingView(name,element) with CollectionBinding
{
//  val key = params.getOrElse("items").toString
//  if(!lists.contains(key)) throw new Exception(s"not items with key == $key")


  //val disp = element.style.display
//  element.attributes.get("data-item-view") match {
//
//    case Some()
//    case None=> dm
//
//  }

  //val template: HTMLElement = this.element.cloneNode(true).asInstanceOf[HTMLElement]
//  element.childNodes.foreach {
//    n =>
//      if(n!=null) {
//        dom.console.info("removing n: " + n.toString)
//        element.removeChild(n)
//      }
//  }
//
//
//
////
  def newItem(mp:Map[String, Any]) = {
    //val item = template.cloneNode(true).asInstanceOf[HTMLElement]
    //item.setAttribute("data-view",("data-view"->"item").toAtt)
    //item
  }

  override def bind(el:HTMLElement) = {
/*
    val items = this.lists(key)
    items.now.foreach{i=>
     // this.element.appendChild(this.newItem(i))
    }
    */
    //super.bind(el)
  }

}