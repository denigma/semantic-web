package controllers
import org.denigma.semantic.controllers.sync.WithSyncWriter
import play.api.libs.json.JsValue
import play.api.templates.Html
import models.{RegisterPicklers, Menu, MenuItem}
import org.denigma.semantic.controllers.UpdateController

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{RDFValue, IRI}
import org.scalax.semweb.rdf.vocabulary.WI
import org.denigma.semantic.controllers.SimpleQueryController
import org.openrdf.model.{Value, Literal, URI}
import scala.util._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame
import org.scalax.semweb.sparql._
import scala.concurrent.duration._
import org.scalax.semweb.sesame._
import spray.caching.{LruCache, Cache}
import scala.concurrent.Future


/*
main application controller, responsible for index and some other core templates and requests
 */
object Application extends PJaxPlatformWith("index") with WithSyncWriter with SimpleQueryController with UpdateController
{

  // and a Cache for its result type
  val queryCache: Cache[Try[List[Map[String, Value]]]] = LruCache(timeToLive = 5 minutes)


  /**
   * Displays logo
   * @param variant
   * @return
   */
  def logo(variant:String) = UserAction.async{
    implicit request=>
      val query = SELECT ( ?("logo") ) WHERE Pat(IRI("http://" + request.domain), IRI(WI.PLATFORM / "has_logo"), ?("logo"))

//      this.queryCache(query.stringValue)(this.select(query).map {
//        res => res.map(_.toListMap)
//      }).map {
//        case Success(res) if res.isEmpty || !res.head.contains("logo")=> this.tellBad(s"logo was not found\n query was: \n ${query.stringValue}")
//        case Success(res) => Ok(res.head("logo").stringValue)
//
//        case Failure(th) => this.tellBad(s"logo loading failed ${th.toString}")
//      }
      val logo = request.domain match {
      case "longevity.org.ua"=> "files/longevity.org.ua/longevity_ukraine.svg"
      case "transhuman.org.ua"=> "files/transhuman.org.ua/ukranian_transhumanism.jpg"
      case "webintelligence.eu"=> "files/webintelligence.eu/denigma.svg"
      case "denigma.org" | "denigma.denigma.de"=> "files/webintelligence.eu/denigma.svg"
      case _=> "files/longevity.org.ua/longevity_ukraine.svg"
    }
      Future.successful{     Ok(logo)    }
  }


  def page(uri:String)= UserAction{
    implicit request=>
      val pg = IRI("http://"+request.domain)
      val page: IRI = if(uri.contains(":")) IRI(uri) else pg / uri

      val text = ?("text")
      val title = ?("title")
      //val authors = ?("authors")
      val pageHtml: Html = Html(
        s"""
            |<article id="main_article" data-view="ArticleView" class="ui teal piled segment">
            |<h1 id="title" data-bind="title" class="ui large header"> ${page.stringValue} </h1>
            |<div id="textfield" contenteditable="true" style="ui horizontal segment" data-html = "text">$text</div>
            |</article>
            """.stripMargin)

      this.pj(pageHtml)(request)

  }



  // and a Cache for its result type
  val menuCache: Cache[Try[Menu]] = LruCache(timeToLive = 5 minutes)

  /**
   * Renders menu for the website
   * @param domainName
   * @return
   */
  def menu(domainName:String = "") =  UserAction.async{
    implicit request=>
      val domain: String = if(domainName=="") request.domain else domainName
      val menuResult = menuCache(domain) {

        val dom =  IRI(s"http://$domain")
        val hasMenu = WI.PLATFORM / "has_menu" iri
        val hasItem = WI.PLATFORM / "has_item" iri
        val hasTitle = WI.PLATFORM / "has_title" iri

        val m = ?("menu")
        val item = ?("item")
        val tlt= ?("title")

        val selMenu = SELECT (item,tlt) WHERE (
          Pat( dom, hasMenu, m ),
          Pat( m, hasItem, item),
          Pat( item, hasTitle, tlt)
          )

        //lg.info(selMenu.stringValue)

        this.select(selMenu).map(v=>v.map{case r=>
          Menu(dom / "menu",domain,r.toListMap.map{case list=>
            for{
              name<-list.get(item.name).collect{ case n:URI=>sesame.URI2IRI(n)}
              title<-list.get(tlt.name).collect{ case l:Literal=>sesame.literal2Lit(l)}

            } yield MenuItem(name,title.label)
          }.flatten)
        })
      }

      menuResult.map[SimpleResult]{
        case Success(res:Menu) =>
          RegisterPicklers.registerPicklers()
          val pickle: JsValue = PicklerRegistry.pickle(res)
          Ok(pickle).as("application/json")
        case Failure(th)=>BadRequest(th.toString)
      }

  }




}
