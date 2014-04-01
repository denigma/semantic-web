package org.denigma.frontend.tests

import org.denigma.views.ListView
import org.scalajs.dom.{Attr, TextEvent, MouseEvent, HTMLElement}
import rx.{Var, Rx}
import scalatags.HtmlTag
import scala.collection.mutable
import scala.collection.immutable.Map

/**
 * Class for testing purposes that makes a long list out of test element
 */
class LongListView(element:HTMLElement, params:Map[String,Any]) extends ListView("lists",element,params){


  override val lists: Map[String, Rx[List[Map[String, Any]]]] = this.extractListRx(this)

//  val items: Var[List[Map[String, String]]] = Var{
//    List(
//      Map("prop"->"value1"),Map("prop"->"value2"),Map("prop"->"value3"),Map("prop"->"value4"),Map("prop"->"value5")
//    )
//
//  }


  override def bindAttributes(el: HTMLElement, ats: mutable.Map[String, Attr]): Unit = {


  }
}
