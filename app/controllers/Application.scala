package controllers
import org.denigma.semantic.controllers.sync.WithSyncWriter
import play.api.libs.json.JsValue
import play.twirl.api.Html
import org.denigma.semantic.controllers.{ShapeController, UpdateController, SimpleQueryController}

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{Res, RDFValue, IRI}
import org.scalax.semweb.rdf.vocabulary.WI
import org.openrdf.model.{Value, Literal, URI}
import views.html
import views.html.index
import scala.util._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame
import org.scalax.semweb.sparql._
import scala.concurrent.duration._
import spray.caching.{LruCache, Cache}
import scala.concurrent.{ExecutionContext, Future}
import auth.{AuthRequest, UserAction}
import ExecutionContext.Implicits.global
import org.scalax.semweb.sesame._




/*
main application controller, responsible for index and some other core templates and requests
 */
object Application extends PJaxPlatformWith("index")
  with WithSyncWriter
  with ShapeController
  //with SimpleQueryController
  with UpdateController
  with WithQuerySearch
{

  var indexes: Map[String, (UserRequestHeader, Option[Html]) => Html] = Map(
    "gero.longevityalliance.org" -> views.html.gero.index.apply _,
    "rybka.org.ua" -> views.html.rybka.index.apply _,
    "www.rybka.org.ua" -> views.html.rybka.index.apply _,
    "default" -> views.html.index.apply _
  )

  def getIndex(request:UserRequestHeader,someHtml:Option[Html] = None) = request.domain match {
    case defined if indexes.contains(defined) => indexes(defined)(request, None)
    case other =>indexes("default")(request,None)
  }

  override def index(): Action[AnyContent] =  UserAction {
    implicit request=>   Ok(getIndex(request))
  }

  override def index[T<:UserRequestHeader](request:T,someHtml:Option[Html] = None) = Ok(getIndex(request,someHtml))

  val queryCache: Cache[Try[List[Map[String, Value]]]] = LruCache(timeToLive = 5 minutes)



  val logos = Map(
    "longevity.org.ua" -> "assets/longevity.org.ua/longevity_ukraine.svg",
    "transhuman.org.ua" -> "assets/transhuman.org.ua/ukranian_transhumanism.jpg",
    "webintelligence.eu" -> "assets/webintelligence.eu/denigma.svg",
    "denigma.org" -> "assets/webintelligence.eu/denigma.svg",
    "denigma.denigma.de" -> "assets/webintelligence.eu/denigma.svg",
    "gero.longevityalliance.org" ->   "assets/webintelligence.eu/denigma.svg",
    "rybka.org.ua" ->   "assets/rybka.org.ua/rybka.jpg",
    "www.rybka.org.ua" ->   "assets/rybka.org.ua/rybka.jpg",
    "default" -> "assets/longevity.org.ua/longevity_ukraine.svg"
  )

  def getLogo(request:UserRequestHeader) = request.domain match {
    case defined if logos.contains(defined) =>logos(defined)
    case default=>logos("default")
  }

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
      val logo =this.getLogo(request)
      Future.successful{     Ok(logo)    }
  }

  protected def renderRybka(uri:String)(implicit request:AuthRequest[AnyContent]):Html =
    uri.toLowerCase() match {
      case u if u.contains("project") => views.html.rybka.pages.project(request)
      case u if u.contains("research") => views.html.rybka.pages.research(request)
      case u if u.contains("collaboration") => views.html.rybka.pages.collaboration(request)
      case u if u.contains("action_plan") => views.html.rybka.pages.action_plan(request)
      case u if u.contains("team") => views.html.rybka.pages.team(request)
      case _ =>
        lg.info(s"some other request for rybka page $uri")
        Html(s"404: page $uri not found")
    }

  protected def renderGero(uri:String)(implicit request:AuthRequest[AnyContent]):Future[Html] = {
    val shape = ?("shape")
    val query = ?("query")
    val page = IRI(uri)
    val wiq = IRI("http://webintelligence.eu/queries/")
    val q = SELECT(shape,query) WHERE(
      Pat(page,shape,WI.PLATFORM.HAS_SHAPE,shape),
      Pat(page,shape, query)
      )
    import org.scalax.semweb.sesame._
    play.api.Logger.debug("RENDER GERO QUERY = "+q.stringValue)
    this.queryCache(q.stringValue) {
      this.select(q).map(res=>res.map(r=>r.toListMap))
    }.flatMap {
      case Failure(th) => Future.failed[Html](th)
      case Success(r) =>
        if(r.isEmpty || !r.head.contains("shape") || !r.head.contains("query")) Future.failed[Html](new Error("no query or shape"))
        play.api.Logger.debug("SHAPE RES "+r.toString)
        val q = r.head("query").stringValue
        val sh = r.head("shape").stringValue
        Future.successful(views.html.grids.datagrid(sh,q)(request))
    }
  }

  protected def getTemplate(uri:String)(implicit request:AuthRequest[AnyContent]): Future[Html] =
    request.domain match{
    case dom if dom.contains("rybka.org")=>Future.successful(this.renderRybka(uri))
    case dom if dom.contains("gero.")=> this.renderGero(uri)(request)
    case other=>
        Future(Html(
        s"""
          |<article id="main_article" data-view="ArticleView" class="ui teal piled segment">
          |<h1 id="title"  class="ui large header"> uri </h1>
          |<div id="textfield" contenteditable="true" style="ui horizontal segment" data-html = "text"></div>
          |</article>
        """.stripMargin
        ))
  }


  def page(uri:String)= UserAction.async{
    implicit request=>
      val pg = IRI("http://"+request.domain)
      val page: IRI = if(uri.contains(":")) IRI(uri) else pg / uri
      this.getTemplate(uri).map(pageHtml=>this.pj(pageHtml)(request))
  }



}
