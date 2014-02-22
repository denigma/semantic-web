package org.denigma.semantic.quering


import scala.util.Try
import com.bigdata.rdf.sail._
import scalax.collection.GraphPredef._
import scala.util.Try
import org.openrdf.query.QueryLanguage
import org.openrdf.model.{URI, Resource, Value}
import org.denigma.semantic.data.SemanticStore

/*
Does various quries on top of RDF store
 */
abstract class QueryWizard{

  type Store <:  SemanticStore

  def db:Store

  lazy val SliceModifier = new SliceQuery(0,db.conf.limit)


  def ask(str:String):Try[Boolean] = db.read{con:BigdataSailRepositoryConnection=>
    this.quickAsk(str)(con)
  }.flatten

  def quickAsk(str:String)(con:BigdataSailRepositoryConnection):Try[Boolean] =
  {
    Try{
      val q = con.prepareBooleanQuery(QueryLanguage.SPARQL,str,"http://denigma.org/resource/")
      q.evaluate()
    }
  }





  /*
 runs query over db
  */
  def query(str:String)(mod:QueryModifier = DefaultQueryModifier):Try[QueryResultLike] = db read{
    implicit r=>
      val query = r.prepareNativeSPARQLQuery(QueryLanguage.SPARQL,str,"http://denigma.org/resource/")
      mod.onQuery(str,r,query)
  }


  def limitedQuery(str:String) = query(str)(SliceModifier)

  /*
  Query that returns paginated result
   */
  def paginatedQuery(str:String,offset:Long,limit:Long = -1) = {
    val lim = if(limit<0) db.conf.limit else limit
    query(str)(SliceQuery(offset,lim))
  }


  /*
  query that returns result with binding
   */
  def queryWithBinding(str:String, bindings: (String,org.openrdf.model.Value)) = query(str)(BindingModifier(bindings))


  /*
adds limit and offset to the query
 */
  def fillQuery(str:String, vars:(String,Value)) = {
//      val q: Query =   QueryFactory.create(str, Syntax.syntaxSPARQL_11)

      val con = db.repo.getReadWriteConnection


      //val upd: UpdateRequest = UpdateFactory.create()


      val upd: BigdataSailUpdate = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,str,"http://denigma.org/resource/")
      val ast = upd.getASTContainer.getOriginalUpdateAST

  }

 def isOfType(s:Resource,tp:Resource)(implicit con: BigdataSailRepositoryConnection): Boolean = {
    val str =       """
                      | PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                      | PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                      |
                      | ASK WHERE {?subject a / rdfs:subClassOf * ?object }
                    """.stripMargin
   this.quickAsk(str)(con).recover{
      case r=>
        db.lg.error(s"isOfType for resource ${s.stringValue()} and type ${tp.stringValue()} FAILED with ${r.toString}"); false
    }.get
  }



//  implicit class MagicQuery(q:Query) {
//
//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }
//
//    def withOffset(offset:Long, always:Boolean = true) = {
//      if(offset>0)
//              if(always || !q.hasOffset) q.setOffset(offset)
//      q
//    }



//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }
//
//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }

//  }


}
