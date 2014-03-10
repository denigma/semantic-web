package org.denigma.semantic.async

import org.specs2.mutable._
import play.api.test.WithApplication

import org.denigma.semantic.test.LoveHater
import scala.util.Try
import org.openrdf.query.TupleQueryResult
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.controllers.{SimpleQueryController, UpdateController}
import scala.concurrent.Future
import play.api.libs.concurrent.Akka
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql._
import org.denigma.semantic.sparql
import scala.collection.JavaConversions._
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading._

class SparqlSpec extends Specification with LoveHater {

  /*
  alias for "this"
   */
  self=>

  class WithTestApp extends WithApplication with SimpleQueryController with UpdateController


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


  "DSL for sparql should" should {

    "send selects with limits and offsets right" in new WithTestApp {
      self.addTestRels()
      val aw: (Future[Try[TupleQueryResult]]) => Try[TupleQueryResult] = this.awaitRead[Try[TupleQueryResult]] _

      implicit val sys = Akka.system(this.app)

      val q: String = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = aw { this.select(q) }
      resFull.isSuccess shouldEqual(true)
      resFull.get.toList.size shouldEqual(6)

      val q2= SELECT( ?("s"), ?("o")) WHERE {
       Pat( ?("s"), IRI("http://denigma.org/relations/resources/loves"), ?("o") )
      }


      val resFull2 = aw { this.select(q2) }
      resFull2.isSuccess shouldEqual(true)
      resFull2.get.toList.size shouldEqual(6)

      val resLimited= aw { this.select(q,0,2) }
      resLimited.isSuccess shouldEqual(true)
      resLimited.get.toList.size shouldEqual(2)

      val ql:sparql.SelectQuery  = q2 OFFSET 0 LIMIT 2

      val resLimited2 = aw { this.select(ql) }
      resLimited2.isSuccess shouldEqual(true)
      resLimited2.get.toList.size shouldEqual(2)


      val resOffset= aw { select(q,2,0) }
      resOffset.isSuccess shouldEqual(true)
      resOffset.get.toList.size shouldEqual(4)

      val qo:sparql.SelectQuery  = q2 OFFSET 2 LIMIT 0

      val resOffset2= aw { select(qo) }
      resOffset2.isSuccess shouldEqual(true)
      resOffset2.get.toList.size shouldEqual(4)
    }


    "insert data right" in new WithTestApp{
      self.addTestRels()


      val del: DeleteQuery = DeleteQuery {
        DELETE (
          DATA (
            Trip(
              IRI("http://denigma.org/actors/resources/Daniel"),
              IRI("http://denigma.org/relations/resources/loves"),
              IRI("http://denigma.org/actors/resources/RDF")
            )
          )
        )
      }

      val ins: InsertQuery = InsertQuery {
        INSERT (
          DATA (
            Trip(
              IRI("http://denigma.org/actors/resources/Anton"),
              IRI("http://denigma.org/relations/resources/hates"),
              IRI("http://denigma.org/actors/resources/Anton")
            )
          )
        )
      }




      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      this.awaitWrite( this.delete(del))

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1

      this.awaitWrite( this.insert(ins))

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

    }
 }
}