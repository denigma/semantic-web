package controllers

import play.api.mvc._
import org.openrdf.model.impl.URIImpl
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.Statement
import org.denigma.semantic.data.{LoveHater, SG}
import SG.db
import scala.collection.JavaConversions._
import scala.collection.immutable._
import org.openrdf.query.{BindingSet, TupleQueryResult, QueryLanguage}
import org.openrdf.model._


object Application extends Controller with LoveHater{
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

  val defQ:String="""
           SELECT ?subject ?object WHERE
           {
              ?subject <http://denigma.org/relations/resources/loves> ?object.
           }
           """



  def query(query:String=defQ) = Action {
    implicit request=>
      this.addTestRels()

      val results = SG.db.query(query)

      Ok(views.html.query(results))

  }



}