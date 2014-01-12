package controllers

import play.api.mvc.Controller
import play.api.mvc._
import org.openrdf.model.impl.URIImpl
import org.openrdf.model.Statement
import org.denigma.semantic.data.SG._
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable.List
import play.api._
import play.api.mvc._
import play.api.libs.json._
import org.openrdf.model.impl.{StatementImpl, URIImpl}
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.Statement
import org.denigma.semantic.data.SG
import SG.db
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.immutable._
import java.util

/**
 * Created by antonkulaga on 12/21/13.
 */
object Tests  extends Controller{
  def editor = Action {
    implicit request =>
      Ok(views.html.test.editor()) //Ok(views.html.page("node","menu","0"))
  }

  def sigma = Action {
    implicit request =>
      Ok(views.html.test.sigma()) //Ok(views.html.page("node","menu","0"))
  }

  def index = Action {
    implicit request=>


      val s: URIImpl = new URIImpl("http://www.bigdata.com/rdf#Daniel")

      //      db.write{
      //        implicit con=>
      //          val p =  new URIImpl("http://www.bigdata.com/rdf#loves")
      //          val o = new URIImpl("http://www.bigdata.com/rdf#RDF")
      //          val st = new StatementImpl(s, p, o)
      //          con.add(st)
      //      }
      val res: scala.List[Statement] = db.read{
        implicit r=>
          val iter: RepositoryResult[Statement] = r.getStatements(null,null,null,true)
          iter.asList().toList
      }.getOrElse(List.empty)

      Ok(views.html.index(res))

  }
}
