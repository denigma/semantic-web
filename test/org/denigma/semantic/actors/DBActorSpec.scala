package org.denigma.semantic.actors

import org.specs2.mutable.SpecificationLike
import org.specs2.mutable._
import play.api.test.WithApplication
import org.denigma.semantic.quering._

import org.denigma.semantic.SP
import akka.testkit._
import play.api.libs.concurrent.Akka
import scala.util.{Try, Success}
import org.openrdf.query.{TupleQueryResult, QueryLanguage}
import com.bigdata.rdf.sail.{BigdataSailTupleQuery, BigdataSailRepositoryConnection}
import scala.collection.immutable.Map
import org.denigma.semantic.controllers._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration._
import scala.concurrent.duration._
import org.denigma.semantic.test.LoveHater
import org.openrdf.repository.RepositoryResult
import org.openrdf.model.{Value, URI, Resource, Statement}
import org.openrdf.model.impl.StatementImpl

class DBActorSpec extends Specification with LoveHater {


  self=>

  type Selection= BigdataSailRepositoryConnection=>QueryResult
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

  def tupleQuery(str:String)(mod:QueryModifier = DefaultQueryModifier):Selection = {
    implicit r=>

      val q= r.prepareTupleQuery(QueryLanguage.SPARQL,str)
      val res: TupleQueryResult = q.evaluate()
      QueryResult.parse(str ,res)
  }

  val q1 =
    """
      |PREFIX  de:   <http://denigma.org/resource/>
      |
      |SELECT  ?property ?object
      |WHERE
      |  { de:Genomic_Instability ?property ?object }
    """.stripMargin


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


  "Actor" should {

    "query on denigma info" in new WithApplication with SimpleQueryController{
      // We need a Fake Application for the Actor system

      implicit val sys = Akka.system(this.app)


      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFile("data/test/test_aging_ontology.ttl")
      val probe1 = new TestProbe(sys)
      val probe2 = new TestProbe(sys)
      val probe3 = new TestProbe(sys)

      val query = self.tupleQuery(q1)(DefaultQueryModifier)

      val q = Data.Read(query)

      probe2.send(SP.reader,q)
      probe3.send(SP.reader,q)
      //probe1.receiveN(1)
      probe2.receiveN(1)
      probe3.receiveN(1)

      probe1.send(SP.reader,q)

      this.awaitRead(this.read(query)) match {
                case Success(value:QueryResult) =>
                  value.bindings.size shouldEqual(4)
                  value
                case _ => self.failure("Actor ask should be successful")
              }
    }

    "make writes" in new WithApplication  with SimpleQueryController with SimpleUpdateController with LoveHater{
      this.addTestRels()
      val loveRes = self.db.read{ con=>con.getStatements(null,loves,null,false).toList }
      val hateRes = self.db.read{ con=>con.getStatements(null,hates,null,false).toList }

      loveRes.isSuccess should beTrue
      loveRes.get.size shouldEqual 6

      hateRes.isSuccess should beTrue
      hateRes.get.size shouldEqual 1

      val wres: Future[Try[Unit]] = this.write[Unit]{con:BigdataSailRepositoryConnection=>
        con.remove(new StatementImpl(Daniel,loves,RDF))
        con.add(new StatementImpl(Anton,hates,Anton))
      }

      this.awaitWrite[Try[Unit]](wres).isSuccess should beTrue

      self.db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

    }

    "send update" in new WithApplication with SimpleQueryController with SimpleUpdateController with LoveHater{
      this.addTestRels()
      self.db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      self.db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      this.awaitWrite(this.updateQuery(d1))
      self.db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1
      this.awaitWrite(this.updateQuery(i1))
      self.db.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.db.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

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
  }
}

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