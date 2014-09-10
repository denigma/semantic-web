package org.denigma.frontend.views

import org.denigma.binding.views.BindableView
import org.scalajs.dom.HTMLElement

class RightMenuView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)

}
