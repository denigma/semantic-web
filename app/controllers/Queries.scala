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
import org.denigma.semantic.{SG, LoveHater}
import org.denigma.semantic.data.QueryResult


object  Queries extends PJaxController("query") with LoveHater{


  val defQ:String="""
           SELECT ?subject ?object WHERE
           {
              ?subject <http://denigma.org/relations/resources/loves> ?object.
           }
                  """

  def main(query:String=defQ) = Action {
    implicit request=>
       Ok(pj("main",views.html.queries.main(query)))
  }

  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)

  def query(query:String=defQ) = Action {
    implicit request=>
      //this.addTestRels()
      SG.db.safeQuery(query,SG.limit,0).map{
        results=>Ok(results.asJson).as("application/json")
      }.recover{
        case e=>
          val er = e.getMessage
          play.Logger.info(s"Query failed \n $query \n with the following error $er")
          Ok(QueryResult.badRequest(query,er)).as("application/sparql-results+json")
      }.get
  }


  def all = Action {
    implicit request=>
      val res: scala.List[Statement] = SG.db.read{
        implicit r=>
          val iter: RepositoryResult[Statement] = r.getStatements(null,null,null,true)
          iter.asList().toList
      }.getOrElse(List.empty)

      Ok(views.html.queries.all(res))

  }


}