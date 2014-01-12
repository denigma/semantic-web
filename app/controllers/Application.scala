package controllers

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
import org.openrdf.query.{BindingSet, TupleQueryResult, QueryLanguage}

object Application extends Controller {
  def index = Action {
    implicit request=>
      val flags = List()//List("United Kingdom","Russia","Ukraine","Israel","Germany","France","Italy","United States","China","Turkey","Spain","Austria").sorted
    val items = List("About","Blog","ILA Manifesto","Take Action","Projects")
      val cont = Items(items,flags)

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

      Ok(views.html.index(cont,res))

  }

  /*
  just a function for testing
   */
  def addRel(sub:String,rel:String,obj:String) = {
    val s: URIImpl = new URIImpl(s"http://denigma.org/actors#$sub")
    val p: URIImpl = new URIImpl(s"http://denigma.org/relations#$rel")
    val o: URIImpl = new URIImpl(s"http://denigma.org/actors#$obj")

    db.write{
      implicit con=>
        val st = new StatementImpl(s, p, o)
        con.add(st)
    }
  }

  def addTestRels() = {
    this.addRel("Daniel","loves","RDF")
    this.addRel("Anton","hates","RDF")
    this.addRel("Daniel","loves","Immortality")
    this.addRel("Liz","loves","Immortality")
    this.addRel("Anton","loves","Immortality")
    this.addRel("Ilia","loves","Immortality")
    this.addRel("Edouard","loves","Immortality")
  }

  def query = Action {
    implicit request=>
      this.addTestRels()

      val flags = List()//List("United Kingdom","Russia","Ukraine","Israel","Germany","France","Italy","United States","China","Turkey","Spain","Austria").sorted
      val items = List("About","Blog","ILA Manifesto","Take Action","Projects")
      val menu = Items(items,flags)
      val query:String =
        """
           SELECT ?subject ?object WHERE
           {
              ?subject <http://denigma.org/relations#loves> ?object.

           }
        """
      val (titles,content) = db.read{
        implicit r=>



          val q = r.prepareTupleQuery(
            QueryLanguage.SPARQL,query
          )

          val results: TupleQueryResult = q.evaluate()
          val names = results.getBindingNames.toList

          var re: List[Map[String,String]] = List.empty[Map[String,String]]
          while(results.hasNext){
            re = binding2List(names,results.next())::re
          }
          (names,re.reverse)

      }.getOrElse((List.empty[String],List.empty[Map[String,String]]))

      Ok(views.html.query(menu,query,titles,content))

  }

  def binding2List(names:List[String],b:BindingSet):Map[String,String] = {
    names.map{
      case name=> (name,b.getValue(name).stringValue())
    }.toMap

  }

}