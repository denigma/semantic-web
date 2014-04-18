package org.denigma.semantic.async

import org.specs2.mutable._

import scala.util.Try
import scala.concurrent.Future
import org.denigma.semantic.test.LoveHater
import play.api.test.WithApplication
import play.api.libs.concurrent.Akka
import org.openrdf.model.impl.URIImpl
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading._
import org.denigma.semantic.controllers.{UpdateController, JsQueryController}
import org.denigma.semantic.sesame._

class DBActorSpec extends Specification with LoveHater {

  /*
  alias for "this"
   */
  self=>

  class WithTestApp extends WithApplication with JsQueryController with UpdateController


  //type writing = BigdataSailRepositoryConnection=>Unit
  /*
  ads some test relationships
   */
  override def  addTestRels() = {
    this.addRel("Daniel","loves","RDF")
    this.addRel("Anton","hates","RDF")
    this.addRel("Daniel","loves","Immortality")
    this.addRel("Liz","loves","Immortality")
    this.addRel("Anton","loves","Immortality")
    this.addRel("Ilia","loves","Immortality")
    this.addRel("Edouard","loves","Immortality")
  }



  val q1 =
    """
      |PREFIX  de:   <http://denigma.org/resource/>
      |
      |SELECT  ?property ?object
      |WHERE
      |  { de:Genomic_Instability ?property ?object }
    """.stripMargin




  "Actor" should {

    "query with limits and offsets" in new WithTestApp {
      self.addTestRels()
      val aw: (Future[Try[QueryResultLike]]) => Try[QueryResultLike] = this.awaitRead[Try[QueryResultLike]] _

      implicit val sys = Akka.system(this.app)

      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"


      val res1: Future[Try[QueryResultLike]] =  this.query(query)
      val res2: Future[Try[QueryResultLike]] =  this.query(query)




      val resFull = aw { this.query(query) }
      resFull.isSuccess shouldEqual(true)

      resFull.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(6)



      val resLimited= aw{ this.query(query,0,2)  }
      resLimited.isSuccess shouldEqual(true)
      resLimited.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(2)

      val resOffset= aw { this.query(query,2,0) }
      resOffset.isSuccess shouldEqual(true)
      resOffset.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(4)


    }


  "query with bindings" in new WithTestApp  {

      self.addTestRels()
      val aw: (Future[Try[QueryResultLike]]) => Try[QueryResultLike] = this.awaitRead[Try[QueryResultLike]] _

      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = aw {  this.query(query) }
      resFull.isSuccess shouldEqual(true)

      resFull.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(6)

      val resBinded= aw {  this.bindedQuery(query,Map("o"->new URIImpl("http://denigma.org/actors/resources/RDF").stringValue())) }
      resBinded.isSuccess shouldEqual(true)

      resBinded.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(1)

    }

  }

    "send update" in new WithTestApp{

      val d1 =
        """
          |PREFIX ac: <http://denigma.org/actors/resources/>
          |PREFIX rel: <http://denigma.org/relations/resources/>
          |
          |DELETE DATA
          |{
          |  ac:Daniel rel:loves ac:RDF .
          |}
        """.stripMargin

      val i1 =
        """
          |PREFIX ac: <http://denigma.org/actors/resources/>
          |PREFIX rel: <http://denigma.org/relations/resources/>
          |
          |INSERT DATA
          |{
          |  ac:Anton rel:hates ac:Anton .
          |}
        """.stripMargin


      self.addTestRels()
      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      this.awaitWrite( this.update(d1))
      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1
      this.awaitWrite( this.update(i1))
      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

    }
}
//    "write updates" in new WithApplication with SemanticController{
//      // We need a Fake Application for the Actor system
//
//    }

//    "query love and hate" in new WithApplication{
//      // We need a Fake Application for the Actor system
//
//      implicit val sys = Akka.system(this.app)
//      val q1 =
//        """
//          |PREFIX  de:   <http://denigma.org/resource/>
//          |
//          |SELECT  ?property ?object
//          |WHERE
//          |  { de:Genomic_Instability ?property ?object }
//        """.stripMargin
//
//      //SP.platformParams.isEmpty should beTrue
//      SP.db.parseFile("data/test/test_aging_ontology.ttl")
//      val probe1 = new TestProbe(sys)
//      val probe2 = new TestProbe(sys)
//      val probe3 = new TestProbe(sys)
//
//      val q = Data.Read(self.tupleQuery(q1)(DefaultQueryModifier))
//
//      probe2.send(SP.reader,q)
//      probe3.send(SP.reader,q)
//      //probe1.receiveN(1)
//      probe2.receiveN(1)
//      probe3.receiveN(1)
//
//      probe1.send(SP.reader,q)
//
//      val res = probe1.expectMsgPF(){
//        case r:Try[QueryResult] =>r
//      }
//      res.isSuccess should beTrue
//      res.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.size shouldEqual(4)
//    }
//  }
//}

//class TestSpec extends Actors { isolated
//  "test1" >> ok
//  "test2" >> ok
//}
//
//abstract class Actors extends
//TestKit(ActorSystem("testsystem", ConfigFactory.parseString(TaskSpec.config)))
//with SpecificationLike with AfterExample {
//
//  override def map(fs: =>Fragments) = super.map(fs) ^ step(system.shutdown, global = true)
//
//  def after = system.shutdown
//}