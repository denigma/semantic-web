package org.denigma.semantic.async

import org.specs2.mutable._
import play.api.test.WithApplication

import org.denigma.semantic.test.LoveHater
import scala.util.Try
import org.openrdf.query.TupleQueryResult
import org.denigma.semantic.controllers._
import scala.concurrent.Future
import play.api.libs.concurrent.Akka
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading._

import org.denigma.semantic.sesame._
import org.denigma.rdf._
import org.denigma.sparql._

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

  val del: Delete = DELETE (
    DATA (
      Trip(
        IRI("http://denigma.org/actors/resources/Daniel"),
        IRI("http://denigma.org/relations/resources/loves"),
        IRI("http://denigma.org/actors/resources/RDF")
      )
    )
  )

  val ins: Insert = INSERT (
    DATA (
      Trip(
        IRI("http://denigma.org/actors/resources/Anton"),
        IRI("http://denigma.org/relations/resources/hates"),
        IRI("http://denigma.org/actors/resources/Anton")
      )
    )
  )



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

      val ql  = q2 OFFSET 0 LIMIT 2

      val resLimited2 = aw { this.select(ql) }
      resLimited2.isSuccess shouldEqual(true)
      resLimited2.get.toList.size shouldEqual(2)


      val resOffset= aw { select(q,2,0) }
      resOffset.isSuccess shouldEqual(true)
      resOffset.get.toList.size shouldEqual(4)

      val qo  = q2 OFFSET 2 LIMIT 0

      val resOffset2= aw { select(qo) }
      resOffset2.isSuccess shouldEqual(true)
      resOffset2.get.toList.size shouldEqual(4)
    }


    "insert data right" in new WithTestApp{
      self.addTestRels()


      val delQ: DeleteQuery = DeleteQuery { del  }

      val insQ: InsertQuery = InsertQuery { ins  }




      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      this.awaitWrite( this.delete(delQ))

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1

      this.awaitWrite( this.insert(insQ))

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

    }

    "make conditional operations" in new WithTestApp{
      self.addTestRels()

      val condFalse = ASK(
        Pat(
          IRI("http://denigma.org/actors/resources/Daniel"),
          IRI("http://denigma.org/relations/resources/hates"),
          IRI("http://denigma.org/actors/resources/Immortality")
        ))

//      val condTrue: AskQuery = ASK(
//        Pat(
//          IRI("http://denigma.org/actors/resources/Ilia"),
//          IRI("http://denigma.org/relations/resources/loves"),
//          IRI("http://denigma.org/actors/resources/Immortality")
//        ))

      val condTrue: AskQuery = ASK(
        Pat(
          ?("subject"),?("predicate"),?("object")
        ))

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      val falseC = this.awaitRead( this.question(condFalse.stringValue))

      falseC.isSuccess should beTrue
      falseC.get should beFalse

      val df = this.awaitWriteCond( this.deleteOnlyIf(DeleteOnlyIf(del,condFalse)))
      df.isSuccess should beTrue
      df.get should beFalse

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 6
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1


      val trueC = this.awaitRead( this.question(condTrue.stringValue))
      trueC.isSuccess should beTrue
      trueC.get should beTrue

      val dt: Try[Boolean] = this.awaitWriteCond( this.deleteOnlyIf(DeleteOnlyIf(del,condTrue)))
      dt.isSuccess should beTrue
      dt.get should beTrue


      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1

      val insF = this.awaitWriteCond( this.insertOnlyIf( InsertOnlyIf(ins,condFalse)))
      insF.isSuccess should beTrue
      insF.get should beFalse

      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 1

      val insT = this.awaitWriteCond( this.insertOnlyIf( InsertOnlyIf(ins,condTrue)))
      insT.isSuccess should beTrue
      insT.get should beTrue


      self.read{ con=>con.getStatements(null,loves,null,false).toList }.get.size shouldEqual 5
      self.read{ con=>con.getStatements(null,hates,null,false).toList }.get.size shouldEqual 2

    }
 }



}