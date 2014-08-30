package controllers.schema

import _root_.controllers.PJaxPlatformWith
import auth.UserAction
import org.denigma.semantic.controllers.SimpleQueryController
import org.openrdf.query.TupleQueryResult
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.{USERS, RDF}
import org.scalax.semweb.sparql._
import play.api.mvc.Result
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import org.scalax.semweb.sparql
import org.scalax.semweb.sesame._

object Schema  extends PJaxPlatformWith("schema") with SimpleQueryController{


  protected def selectByClass(cl:IRI): Future[Result] = {
    val sl = SELECT(?("sub")) WHERE Pat(?("sub"), RDF.TYPE, cl)

    this.select(sl) map {
      case Success(res) =>
        val listMap = res.toListMap
        val str = listMap.foldLeft("\n"){ (acc,el)=>
          acc+el.toString()+"\n"

        }
        Ok(str).as("text/html")
      case Failure(th) => this.tellBad("cannot")

    }
  }


  def instancesOfClass(cl:IRI)= UserAction.async {implicit request=>this.selectByClass(cl)  }

  def allUsers() = UserAction.async{ implicit request=> this.selectByClass(USERS.classes.User) }


}
