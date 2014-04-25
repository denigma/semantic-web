package controllers
import models.RegisterPicklers._
import play.api.mvc._
import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.users.Accounts
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.templates.Html
import models.{RegisterPicklers, Menu, MenuItem}
import org.denigma.semantic.controllers.UpdateController

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sparql._
import org.scalax.semweb.sparql.Pat
import org.denigma.semantic.controllers.SimpleQueryController
import org.denigma.semantic.reading.selections._
import org.openrdf.model.{Literal, URI}
import scala.concurrent.Future
import scala.util._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.PicklerRegistry._
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame

import org.scalax.semweb.sesame._
import spray.caching.{LruCache, Cache}
import spray.caching


/*
main application controller, responsible for index and some other core templates and requests
 */
object Application extends PJaxPlatformWith("") with WithSyncWriter with SimpleQueryController with UpdateController
{

  def index(): Action[AnyContent] =  UserAction {
    implicit request=>

      Ok(views.html.index(request))
  }



  // and a Cache for its result type
  val menuCache: Cache[Try[Menu]] = LruCache()
  /**
   * Renders menu for the website
   * @param domainName
   * @return
   */
  def menu(domainName:String = "") =  UserAction.async{
    implicit request=>
      val domain = if(domainName=="") request.domain else domainName
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

  def page(uri:String) =  UserAction {
    implicit request=>
      val res: Html = views.html.index(request)
      Ok(res)
  }


}
