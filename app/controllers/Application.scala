package controllers
import org.denigma.semantic.controllers.sync.WithSyncWriter
import play.api.libs.json.JsValue
import play.twirl.api.Html
import org.denigma.semantic.controllers.UpdateController

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{RDFValue, IRI}
import org.scalax.semweb.rdf.vocabulary.WI
import org.denigma.semantic.controllers.SimpleQueryController
import org.openrdf.model.{Value, Literal, URI}
import scala.util._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame
import org.scalax.semweb.sparql._
import scala.concurrent.duration._
import spray.caching.{LruCache, Cache}
import scala.concurrent.Future
import auth.UserAction


/*
main application controller, responsible for index and some other core templates and requests
 */
object Application extends PJaxPlatformWith("index") with WithSyncWriter with SimpleQueryController with UpdateController
{

  override def index(): Action[AnyContent] =  UserAction {
    implicit request=>
      if(request.domain.contains("rybka.org"))
        Ok(views.html.rybka.index(request))
      else
        Ok(views.html.index(request))
  }


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
      case "longevity.org.ua"=> "assets/longevity.org.ua/longevity_ukraine.svg"
      case "transhuman.org.ua"=> "assets/transhuman.org.ua/ukranian_transhumanism.jpg"
      case "webintelligence.eu"=> "assets/webintelligence.eu/denigma.svg"
      case "denigma.org" | "denigma.denigma.de"=> "assets/webintelligence.eu/denigma.svg"
      case "rybka.org.ua" => "assets/rybka.org.ua/rybka.jpg"

      case _=> "assets/longevity.org.ua/longevity_ukraine.svg"
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
      //data-bind="title"
      val pageHtml: Html = Html(
        s"""
            |<article id="main_article" data-view="ArticleView" class="ui teal piled segment">
            |<h1 id="title"  class="ui large header"> ${/*page.stringValue*/uri} </h1>
            |<div id="textfield" contenteditable="true" style="ui horizontal segment" data-html = "text">$text</div>
            |</article>
            """.stripMargin)

      this.pj(pageHtml)(request)

  }



}
