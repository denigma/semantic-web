package controllers
import play.api.mvc._
import org.openrdf.model.impl.URIImpl
import org.openrdf.repository.RepositoryResult
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.model._
import java.io.File
import org.openrdf.rio.RDFFormat
import play.api.libs.json.{JsValue, JsNull, JsObject, Json}
import play.api.Play.current
import play.api.templates.Html
import scala.util.Try
import com.bigdata.rdf.sparql.ast.IQueryNode
import org.topbraid.spin._
import org.denigma.semantic._
import org.denigma.semantic.data.QueryResult
/**
 * Created by antonkulaga on 2/4/14.
 */
object Schema  extends PJaxController("schema") {

  def sankey() = Action {
    implicit request=>
      //Ok(pj("sankey",views.html.schema.sankey()))
      Ok(views.html.schema.sankey())
  }

  def distortion() = Action {
    implicit request=>
      Ok(pj("distortion",views.html.schema.distortion()))
  }

}
