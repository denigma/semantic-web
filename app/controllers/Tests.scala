package controllers

import play.api.mvc._
import org.openrdf.model.impl.{StatementImpl, URIImpl}
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.Statement
import org.denigma.semantic.data.SG
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


}
