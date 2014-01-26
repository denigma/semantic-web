package controllers

import play.api.mvc._
import org.openrdf.model.impl.{StatementImpl, URIImpl}
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.Statement
import org.denigma.semantic.SG
import SG.db
import scala.collection.JavaConversions._
import org.openrdf.query.{BindingSet, TupleQueryResult, QueryLanguage}
import scala.collection.immutable._

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

  def test = Action {
    implicit request =>
      val str =
        """
          |PREFIX  bds:  <http://www.bigdata.com/rdf/search#>
          |
          |SELECT  ?subject ?property ?object
          |WHERE
          |  { ?object bds:search "aging" .
          |    ?subject ?property ?object
          |  }
          |LIMIT   50
          |
        """.stripMargin
    val sp = SG.db.asTemplate(str,"testTemplate")
//    val sp = SG.db.asSpin(str)

      Ok(sp)
  }


}
