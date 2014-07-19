package org.denigma.frontend.views

import java.util.Date

import org.denigma.binding.extensions._
import org.denigma.binding.picklers.rp
import org.denigma.binding.semantic.ModelCollection
import org.denigma.controls.general.CodeMirrorView
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.messages.StringQueryMessages
import rx._
import rx.ops._

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}
import scalatags.Text.Tag

/**
 * View for paper viewer
 */
class QueryView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any] ) extends ModelCollection
{


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


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





  val submit = Var(this.createMouseEvent())

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

  val reset = Var(this.createMouseEvent())

  reset handler {
    query() = default
    dom.console.log("reset click")
  }

  val uploadClick = Var(this.createMouseEvent())

}

class SelectQueryView(elem:HTMLElement, val params:Map[String,Any] = Map.empty[String,Any]) extends CodeMirrorView(elem,params){
  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def strings: Map[String, Rx[String]] = this extractStringRx this

  override def tags: Map[String, Rx[Tag]] = this extractTagRx this

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = this extractMouseEvents this

  override val name = "select"


  override def bindView(el:HTMLElement) {

    super.bindView(el)

  }

}