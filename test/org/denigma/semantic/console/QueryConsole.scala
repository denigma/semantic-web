package org.denigma.semantic.console

import org.denigma.semantic.vocabulary.WI

/*
just a code to copy-paste to play console
*/
object QueryConsole {
  import scala.collection.JavaConversions._
  import scala.concurrent.duration._
  import play.core._
  import akka.util.Timeout
  import org.openrdf.model.impl._
  import org.openrdf.query._

  import com.bigdata.rdf.sparql.ast._
  import com.bigdata.rdf.sail._

  //
  import org.denigma.semantic.test.LoveHater
  import org.denigma.semantic.platform.SP
  import org.denigma.semantic.controllers.sync._
  import org.denigma.semantic.commons._
  import org.denigma.semantic.reading.selections._


  implicit val writeTimeout:Timeout = Timeout(5 seconds)

  implicit val readTimeout:Timeout = Timeout(5 seconds)

  SP.cleanLocalDb()
  SP.platformParams = new StatementImpl(new URIImpl("http://hello.subject.world.com"),new URIImpl("http://hello.property.world.com"),new URIImpl("http://hello.object.world.com"))::SP.platformParams

  //needed for testing
  class ConsoleApp extends StaticApplication(new java.io.File(".")) with SyncUpdateController with SyncSimpleController with LoveHater

  val app: ConsoleApp = new ConsoleApp

  val prop = "<http://denigma.org/relations/resources/loves>"


//  val s1 =s"SELECT ?subject ?property ?object WHERE { ?subject $prop ?object .}"
//  val s2 =s"SELECT ?subject ?property ?object WHERE { ?subject ?property ?object . }"
  val s =s"""SELECT ?subject ?property ?object WHERE { ?subject ?property ?object . FILTER regex(STR(?property), "hates") . }"""
  def cl(o:AnyRef) = o.getClass

  //def r = this.writeCon.clear()
  def t() = app.addTestRels()

  def upd(str:String): BigdataSailUpdate = app.writeConnection.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,str,WI.RESOURCE)
  val con = app.readConnection
  def sel(str:String): BigdataSailTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,str,WI.RESOURCE)
  def contR(q:BigdataSailTupleQuery) =  q.getASTContainer
  def astR(q:BigdataSailTupleQuery) = contR(q).getOriginalAST
  def contU(u:BigdataSailUpdate) =  u.getASTContainer
  def astU(u:BigdataSailUpdate) = contU(u).getOriginalUpdateAST
  def res(q:BigdataSailTupleQuery) = q.evaluate().toList

  // test code init start
  t()
  val q = sel(s)
  val fr = con.getValueFactory
  val lex = con.getTripleStore.getLexiconRelation

  val a = astR(q)
  val w = a.getWhereClause
  val ch = w.getChildren.toList
  val f: FilterNode = ch.collectFirst{case p:FilterNode=>p}.get
  val fn: FunctionNode = f.args().collectFirst{case p:FunctionNode=>p}.get

  app.var2Str("property")


  //
//  def updateConsole = {
//
//
//    //COPY to PLAY CONSOLE
//    //COPY to PLAY CONSOLE

//
//    new StaticApplication(new java.io.File("."))
//    val repo = SG.db.repo
//    val con: BigdataSailRepositoryConnection = SG.db.repo.getConnection
//    con.setAutoCommit(false)
//    val f = con.getValueFactory
//
//    val lex = con.getTripleStore.getLexiconRelation
//    val hates = f.createURI("http://denigma.org/relations/resources/hates")
//    val value = f.createURI("http://denigma.org/actors/resources/Daniel")
//    //val query: String = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"
//
//    //this.addFullRel(s"http://denigma.org/actors/resources/$sub",s"http://denigma.org/relations/resources/$rel",s"http://denigma.org/actors/resources/$obj")
//
//
val simple =
  """
    |
  """.stripMargin

val q1 =
  """
    |PREFIX  de:   <http://denigma.org/resource/>
    |
    |SELECT  ?property ?object
    |WHERE
    |  { de:Genomic_Instability ?property ?object }
  """.stripMargin


//    val query =   """
//      PREFIX dc:  <http://purl.org/dc/elements/1.1/>
//    PREFIX dcmitype: <http://purl.org/dc/dcmitype/>
//    PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
//
//    INSERT
//    { GRAPH <http://example/bookStore2> { ?book ?p ?v } }
//    WHERE
//    { GRAPH  <http://example/bookStore>
//    { ?book dc:date ?date .
//    FILTER ( ?date < "2000-01-01T00:00:00-02:00"^^xsd:dateTime )
//      ?book ?p ?v
//    }
//    } ;
//
//    WITH <http://example/bookStore>
//    DELETE
//    { ?book ?p ?v }
//    WHERE
//    { ?book dc:date ?date ;
//      dc:type dcmitype:PhysicalObject .
//      FILTER ( ?date < "2000-01-01T00:00:00-02:00"^^xsd:dateTime )
//      ?book ?p ?v
//    }
//   """.stripMargin
//    val baseURI = "http://denigma.org/relations/resources/"
//
//    //    val lh: LoveHater with Object = new LoveHater {}
//    //    lh.addTestRels()
//
//    val u: BigdataSailUpdate = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,query,baseURI)
//    val cont = u.getASTContainer
//    val au = cont.getOriginalUpdateAST
//    u.getParsedUpdate
//    u.execute()
//
//
//  }
//
//
//
//  def queryConsole = {
//
//
//
                                                                                              //    //COPY to PLAY CONSOLE
                                                                                              //    import play.core._
                                                                              //    import org.denigma.semantic._
                                                                              //    import org.denigma.semantic.SP._
//    import org.openrdf.model._
//    import org.openrdf.model.impl._
//    import com.bigdata.rdf.sail._
//    import org.openrdf.query._
//    import org.denigma.semantic.LoveHater
//    import scala.collection.immutable._
//    import org.denigma.semantic.quering._
//    import com.bigdata.rdf.sparql.ast._
//    import scala.collection.JavaConversions._
                                                                                                                                                                                                                                                                                                                                    //    import com.bigdata.rdf.model._
                                                                                                                                                                                                                                                                                                                                      //
                                                                                                                                                                                                                                                                                                                                          //    import com.bigdata.bop._
                                                                                                                                                                                                                                                                                                                                          //    import play.api._
                                                                                                                                                                                                                                                                                                                                          //    import play.api.Logger
                                                                                                                                                                                                                                                                                                                                            //
                                                                                                                                                                                                                                                                                                                                              //    new StaticApplication(new java.io.File("."))
                                                                                                                                                                                                                                                                                                                                                //    val repo = SG.db.repo
                                                                                                                                                                                                                                                                                                                                                  //    val con: BigdataSailRepositoryConnection = SG.db.r                                                                                                                                    epo.getConnection
//    val f = con.getValueFactory
//
//    val lex = con.getTripleStore.getLexiconRelation
//    val hates = f.createURI("http://denigma.org/relations/resources/hates")
//    val value = f.createURI("http://denigma.org/actors/resources/Daniel")
//    val query: String = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"
//
//    val baseURI = "http://denigma.org/relations/resources/"
//
//    val lh: LoveHater with Object = new LoveHater {}
//    lh.addTestRels()
//
                                                                                                                                                                              //    val q: BigdataSailTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,query, ng                                                                                                                                                                                                                                                                   baseURI)
                                                                                                                                                    //    val cont = q.getASTContainer
                                                                                                                                                                                                //    val ast: QueryRoot = cont.getOrigi                                                                                                                                                                  nalAST
                                                                                                                   //
          //    q.getParsedQuery
//    q.setBinding("s",value)
//
                    //    val res: TupleQueryResult = q.evaluate()
                          //    val qr    = QueryResult.parse(query,res)
//    val bs = qr.bindings
//    bs.foreach(b=>println(b))
//    bs.size
//
//
//    def bindQuery(q:BigdataSailTupleQuery, bindings:(String,BigdataValue)*) = {
//      val cont: ASTContainer = q.getASTContainer
//
//      val ast = cont.getOriginalAST
                                                                                                                                                                                                                                                                                                                  //      val lex = q.getTripleStore.getLexiconRelation
                                                                                                                                                                                                                                                                                                                  //      val terms = bindings.map(_._2)
//      lex.addTerms(terms.toArray,terms.size,true)
//      val params: Map[String, ConstantNode] = bindings.map{
//        case (key,value:BigdataValue)=>
//          val iv = value.getIV.asInstanceOf[com.bigdata.rdf.internal.IV[com.bigdata.rdf.model.BigdataValue, _]]
//          iv.setValue(value)
//          val cn = new ConstantNode(iv)
//          (key,cn)
//      }.toMap
//
//      val pat = ast.getGraphPattern
//      val ptns = pat.getChildren.toList
//      ptns.map{case stn:StatementPatternNode=>stn}
//
//      ptns.view.filter(v=>v.isInstanceOf[StatementPatternNode]).foreach{
//        case stn:StatementPatternNode=>
//
//        Map(0->stn.s(),1->stn.p(),2->stn.o()).foreach{
//          case (i,v:VarNode)=>
//
//            val n: String = v.getValueExpression.getName
//            if(params.contains(n))   stn.setArg(i,params(n))
//          case _ => // skip
//        }
//      }
//
//      cont.setOriginalAST(ast)
//      q
//    }
//  }
}
