package org.denigma.frontend.views

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.semantic.models.{RemoteModelView, RemoteLoadView}
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.core.Var

import scala.collection.immutable.Map

class PageView(val elem:HTMLElement,val params:Map[String,Any]) extends  RemoteLoadView
{
  val saveClick: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }


  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = binders = RemoteModelView.defaultBinders(this)
}
