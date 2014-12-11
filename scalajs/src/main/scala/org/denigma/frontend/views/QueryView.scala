package org.denigma.frontend.views

import java.util.Date

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.BindableView
import org.denigma.controls.binders.CodeBinder
import org.denigma.semantic.models.collections.ModelCollection
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.messages.StringQueryMessages
import rx._
import rx.ops._

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

/**
 * View for paper viewer
 */
class QueryView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any] ) extends ModelCollection
{



  val path = (params.get("path") map (_.toString)).get


  def genId(): String = js.eval(""" 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {var r = Math.random()*16|0,v=c=='x'?r:r&0x3|0x8;return v.toString(16);}); """).toString

  def select(query:String): Future[SelectResults] = {
    val data = StringQueryMessages.Select(query,this.id,genId(), new Date())
    sq.post(path,data)(rp):Future[SelectResults]
  }

  val default =   """
                    |PREFIX de: <http://denigme.de/resource/>
                    |PREFIX pl: <http://webintelligence.eu/platform/>
                    |PREFIX wi: <http://webintelligence.eu/resource/>
                    |PREFIX foaf: <http://xmlns.com/foaf/0.1/>
                    |PREFIX dc: <http://purl.org/dc/elements/1.1/>
                    |
                    |SELECT  ?property ?object
                    |WHERE
                    |  { de:Genomic_Instability ?property ?object }
                  """.stripMargin

  val query = Var(  default  )
  val errors = Var("")

  val hasErrors: Rx[Boolean] = errors.map(e=>e!="")





  val submit = Var(EventBinding.createMouseEvent())

  submit.handler{
    val q = query.now
    dom.console.log(q)
    this.select(q).onComplete{
      case Success(res)=>dom.alert(res.toString())
      case Failure(th)=>
        dom.alert("query error = "+th.toString)
        this.errors() = th.getMessage
    }

  }

  val reset = Var(EventBinding.createMouseEvent())

  reset handler {
    query() = default
    dom.console.log("reset click")
  }

  val uploadClick = Var(EventBinding.createMouseEvent())

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = binders =  BindableView.defaultBinders(this)

}

class SelectQueryView(val elem:HTMLElement, val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{
  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override val name = "select"

  override protected def attachBinders(): Unit = this.withBinders(new CodeBinder(this))

}