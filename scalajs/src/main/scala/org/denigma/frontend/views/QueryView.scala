package org.denigma.frontend.views

import java.util.Date

import org.denigma.binding.binders.collections.KeyValue.StringBinder
import org.denigma.binding.binders.collections.{KeyValue, MapItemsBinder}
import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.collections.{ListCollectionView, CollectionView}
import org.denigma.binding.views.{OrganizedView, BindableView}
import org.denigma.controls.binders.CodeBinder
import org.denigma.semantic.models.WithShapeView
import org.denigma.semantic.models.collections.ModelCollection
import org.denigma.semantic.rdf.ShapeInside
import org.denigma.semantic.shapes.ShapeView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.messages.Results.SelectResults
import org.scalax.semweb.messages.StringQueryMessages
import org.scalax.semweb.rdf.RDFValue
import org.scalax.semweb.shex.Shape
import rx._
import rx.core.Var
import rx.ops._

import scala.collection.immutable._
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

/**
 * View for paper viewer
 */
class QueryView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any] ) extends CollectionView
{

  override type Item = List[(String,RDFValue)]
  override type ItemView = BindableView

  override val items = Var(List.empty[Item])

  val headers = Var(List.empty[String])


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
                    |  { ?subject ?property ?object }
                  """.stripMargin

  val query = Var(  default  )
  val errors = Var("")

  val hasErrors: Rx[Boolean] = errors.map(e=>e!="")


  val submit = Var(EventBinding.createMouseEvent())

  override def attributesToParams(el:HTMLElement) = super.attributesToParams(el) + ("headers"->this.headers)

  submit.handler{
    val q = query.now
    //dom.console.log(q)
    this.select(q).onComplete{
      case Success(res)=>
        val data: List[List[(String, RDFValue)]] = res.rows.map(mp=>mp.toList)
        //items() = List.empty
        items() = data
        this.headers() = res.headers
        //dom.alert(res.toString())
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
  override protected def attachBinders(): Unit = binders = new CodeBinder(this)::new NavigationBinding(this)::Nil

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("item"->item)) {
    (e,m)=>new QueryRow(e,item,m)
  }
}

class CellView(val elem:HTMLElement,val cell:(String,RDFValue),val params:Map[String,Any]) extends BindableView {


  lazy val value = cell._2.stringValue

  override protected def attachBinders(): Unit = binders =  List(new StringBinder(this,cell._1,value))
  //new GeneralBinder(this)::Nil

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}

class QueryRow(val elem:HTMLElement,line:List[(String,RDFValue)],val params:Map[String,Any]) extends BindableView  with CollectionView {

  override protected def attachBinders(): Unit = binders = new GeneralBinder(this)::Nil

  override def activateMacro(): Unit =  { extractors.foreach(_.extractEverything(this))}

  override type Item = (String,RDFValue)

  override type ItemView  = BindableView

  override val items = Var(List.empty[(String,RDFValue)])

  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    //dom.console.log("QUERY ROW IS = "+line.toString)
    items() = line
  }

  override def newItem(item: Item): ItemView = this.constructItem(item,Map("item"->item)) {
    (e,m)=>new CellView(e,item,m)
  }
/*


  override def addItemView(item:Item,iv:ItemView):ItemView = {
    span.parentElement.insertBefore(iv.viewElement,span)
    iv match {
      case b:ChildView=>  this.addView(b)
    }
    dom.console.log(s"ITEM VIEW FOR ${item.toString}")
    itemViews = itemViews + (item->iv)
    iv.bindView(iv.viewElement)
    //dom.console.log(iv.viewElement.innerHTML)
    iv
  }
*/

}

class QueryHeaderView(val elem:HTMLElement, val params:Map[String,Any], father:Option[OrganizedView]) extends  BindableView  with CollectionView{

  override lazy val items = this.resolveKey("headers"){case h:Var[List[String]]=>h}

  protected def attachBinders(): Unit = binders = new GeneralBinder(this)::Nil

  def activateMacro(): Unit =  { extractors.foreach(_.extractEverything(this))}

  val disp = elem.style.display

  override type Item = (String)
  override type ItemView = BindableView


  def newItem(item:Item):ItemView = {
    //debug("ITEM="+item.toString())
    val v = this.constructItem(item){  (el,mp)=>  BindableView(el,mp) }
    v.withBinders(new StringBinder(v,"name",item))
  }


}