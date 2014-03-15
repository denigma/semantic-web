package controllers

import play.api.mvc._
import org.openrdf.model.impl.StatementImpl
import org.openrdf.query.BindingSet


/*
test controller
not working yet
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
          |FROM <http://webintelligence.eu/config/>
          |WHERE
          |  {
          |    ?subject ?property ?object
          |  }
          |LIMIT   50
          |
        """.stripMargin
///    val sp = SG.db.asTemplate(str,"testTemplate")
//    val sp = SG.db.asSpin(str)

      Ok(str)
  }

  def scalaJS = Action {
    implicit request=>
      Ok(views.html.test.learnScalaJavaScript())

  }



}
