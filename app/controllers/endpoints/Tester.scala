package controllers.endpoints

import auth.UserAction
import controllers.WithQuerySearch
import org.denigma.binding.picklers.rp
import org.denigma.semantic.controllers.ShapeController
import org.openrdf.query.TupleQueryResult
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.{RDF, RDFS}
import org.scalax.semweb.shex.{NameTerm, PropertyModel, Shape}
import org.scalax.semweb.sparql._
import controllers.tests.Tests._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.scalajs.spickling.playjson.builder
import org.scalax.semweb.sesame._
import play.twirl.api.Html


import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object Tester extends ShapeLoader with ShapeController with WithQuerySearch
{

  def allShapes = UserAction.async{
    //val id = ?("id")
    //val ids = SELECT( id ) WHERE Pat(id, RDF.TYPE ,Shape.rdfType )
    shapes("all") {this.loadAllShapes() }.map{
      case Success(shs)=>
        val p = rp.pickle(shs)
        Ok(p).as("application/json")
      case Failure(d)=>
        play.api.Logger.error("STACK = "+d.getStackTrace.mkString("\n"))
        BadRequest(s"FAILURE: "+d.toString)

    }
  }

  def allData = UserAction.async{

    val sel = SELECT(?("s"),?("p"),?("o")) WHERE Pat(?("s"),?("p"),?("o"))
    this.select(sel).map{
        case Success(data) => Ok(data.toListMap.toString()).as("text/plain")
        case Failure(d) => BadRequest(s"FAILURE: "+d.toString)
      }
  }

  def query(res:String) = UserAction.async{implicit request=>
    import org.scalax.semweb.sesame._
    this.queryFor(IRI(res)).flatMap{
      case query=>
        val result = s"QUERY = <br> $query <hr>"
        this.select(query).map{
          case Success(elements)=>
            val data = elements.toListMap.foldLeft("<hr>")( (acc,el)=> acc + "<br>"+el.toString )
            Ok(Html(result+data))
          case Failure(th)=>
            Ok(Html(result))
        }
    }
  }

  def shapedQuery(res:String,shape:String) = UserAction.async{implicit request=>
    import org.apache.commons.lang.StringEscapeUtils._

    def model2row(p:PropertyModel): String = {
      //play.api.Logger.info("property model = "+p.properties.toString)
      val els = p.properties.flatMap{case (iri,values)=>values}
      els.foldLeft("<tr>")((acc,el)=>acc+"<td>"+ escapeXml(el.stringValue)+"</td>")+"<tr>"
    }

    def shape2row(sh:Shape): String = {
      import org.apache.commons.lang.StringEscapeUtils._
      sh.arcSorted().map(_.name).foldLeft("<tr>"){
        case (acc,NameTerm(prop))=>acc+"<th>" + escapeXml(prop.stringValue) +"</th>"
        case (acc,other)=>acc+"<th>"+"other = "+other.toString+"</th>"
      }+"</tr>"
    }
    import org.scalax.semweb.sesame._
    this.queryFor(IRI(res)).flatMap{
      case query=>
        val begin ="<table border = 1>"
        val end = "</table>"
        this.stringSelectWithShapeRes(query,IRI(shape)).map{
          case Success((sh,list))=>
            val header = begin+shape2row(sh)
            val htstring: String = list.foldLeft(header)(  (acc,el)=>acc+model2row(el)    )+end
            Ok(Html(htstring))
          case Failure(th)=> Ok(Html(s"broken shape $shape with query $res"))
        }
    }
  }

}
