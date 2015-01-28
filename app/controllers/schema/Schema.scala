package controllers.schema

import _root_.controllers.PJaxPlatformWith
import auth.UserAction
import org.denigma.binding.picklers.rp
import org.denigma.semantic.controllers.{ShapeController, SimpleQueryController}
import org.openrdf.query.TupleQueryResult
import org.scalax.semweb.rdf.{BlankNode, Res, IRI}
import org.scalax.semweb.rdf.vocabulary.{USERS, RDF}
import org.scalax.semweb.sparql._
import play.api.mvc.Result
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import org.scalax.semweb.sparql
import org.scalax.semweb.sesame._
import org.scalax.semweb._
import org.scalajs.spickling.playjson.builder

object Schema  extends PJaxPlatformWith("schema") with ShapeController{


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

  def arcView(id:String) = UserAction.async{implicit request=>
    val arc = id match {
      case b if b.startsWith("_:") =>loadArc(BlankNode(b))
      case uri if uri.contains(":")=>loadArc(IRI(uri))
      case other=>loadArc(BlankNode(other))

    }
    arc.map{
      case Success(a)=>
        val js = rp.pickle(a)
        Ok(js)
      case Failure(ev)=>
        this.tellBad(s"cannot load arc rule for id = $id")
    }

  }



}
