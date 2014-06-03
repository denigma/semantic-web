package controllers

import play.api.libs.json.JsValue

import org.scalax.semweb.rdf.vocabulary._
import play.api.mvc._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.WI
import org.denigma.semantic.controllers.SimpleQueryController
import org.openrdf.model.{Literal, URI}
import scala.util._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sesame
import org.scalax.semweb.sparql._
import scala.concurrent.duration._
import org.scalax.semweb.sesame._
import spray.caching.{LruCache, Cache}
import auth.UserAction
import scala.concurrent.Future
import org.denigma.binding.models._

/**
 * Shows menus
 */
object Menus extends Controller with SimpleQueryController with PickleController
{
  // and a Cache for its result type
  val menuCache: Cache[Try[List[MenuItem]]] = LruCache(timeToLive = 5 minutes)

  implicit def register = RegisterPicklers.registerPicklers

  type ModelType = MenuItem


  val dom =  IRI(s"http://domain")


  /**
   * Renders menu for the website
   * @param name menu name
   * @return
   */
  def menu(name:String = "top") =  UserAction.async{
    implicit request=>

      //val domain: String = if(domainName=="") request.domain else domainName
      val domain = request.domain
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
          r.toListMap.map{case list=>
            for{
              name<-list.get(item.name).collect{ case n:URI=>sesame.URI2IRI(n)}
              title<-list.get(tlt.name).collect{ case l:Literal=>sesame.literal2Lit(l)}

            } yield MenuItem(name,title.label)   }.flatten  })
      }

      menuResult.map[Result]{
        case Success(res:List[MenuItem]) =>
          RegisterPicklers.registerPicklers()
          val pickle: JsValue = PicklerRegistry.pickle(res)
          Ok(pickle).as("application/json")
        case Failure(th)=>BadRequest(th.toString)
      }
  }

}
