package controllers

import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.files.SemanticFileParser
import play.api.mvc.Action
import play.api.mvc._
import java.io.File
import play.api.libs.json.{JsError, JsObject, Json}
import play.api.Play
import play.api.Play.current
import org.denigma.semantic.writing.DataWriter
import org.denigma.semantic.controllers.sync.WithSyncWriter
import org.denigma.semantic.files.SemanticFileParser
import com.bigdata.counters.AbstractCounterSet
import org.denigma.semantic.users.Accounts
import scala.util._
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable._
import play.api.libs.json.{JsValue, Json}
import org.denigma.semantic._
import org.denigma.semantic.reading.selections.SelectResult
import org.denigma.semantic.controllers.QueryController
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import org.denigma.semantic.reading.QueryResultLike
import play.api.libs.json.JsObject
import play.api.libs.json.JsObject
import play.api.mvc.SimpleResult
import scala.util.Success
import scala.util.Failure
import play.api.libs.json.JsObject
import scala.concurrent.Future
/**
 * For longevity website
 */
object Longevity extends Controller
{

  def index(): Action[AnyContent] =  UserAction {
    implicit request=>
      Ok(views.html.longevity.index(request))
  }

}
