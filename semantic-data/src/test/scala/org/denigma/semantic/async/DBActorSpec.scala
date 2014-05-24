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
import org.scalax.semweb.sesame._
import org.denigma.semantic.WithSemanticPlugin

class DBActorSpec extends Specification with LoveHater {

  /*
  alias for "this"
   */
  self=>

  class WithTestApp extends WithSemanticPlugin with JsQueryController with UpdateController


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


      val res1: Future[Try[QueryResultLike]] = this.query(query)
      val res2: Future[Try[QueryResultLike]] = this.query(query)


      val resFull = aw {
        this.query(query)
      }
      resFull.isSuccess shouldEqual (true)

      resFull.map(qr => qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual (6)


      val resLimited = aw {
        this.query(query, 0, 2)
      }
      resLimited.isSuccess shouldEqual (true)
      resLimited.map(qr => qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual (2)

      val resOffset = aw {
        this.query(query, 2, 0)
      }
      resOffset.isSuccess shouldEqual (true)
      resOffset.map(qr => qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual (4)


    }


    "query with bindings" in new WithTestApp {

      self.addTestRels()
      val aw: (Future[Try[QueryResultLike]]) => Try[QueryResultLike] = this.awaitRead[Try[QueryResultLike]] _

      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = aw {
        this.query(query)
      }
      resFull.isSuccess shouldEqual (true)

      resFull.map(qr => qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual (6)

      val resBinded = aw {
        this.bindedQuery(query, Map("o" -> new URIImpl("http://denigma.org/actors/resources/RDF").stringValue()))
      }
      resBinded.isSuccess shouldEqual (true)

      resBinded.map(qr => qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual (1)

    }


    "send update" in new WithTestApp {

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
      self.read { con => con.getStatements(null, loves, null, false).toList}.get.size shouldEqual 6
      self.read { con => con.getStatements(null, hates, null, false).toList}.get.size shouldEqual 1


      this.awaitWrite(this.update(d1))
      self.read { con => con.getStatements(null, loves, null, false).toList}.get.size shouldEqual 5
      self.read { con => con.getStatements(null, hates, null, false).toList}.get.size shouldEqual 1
      this.awaitWrite(this.update(i1))
      self.read { con => con.getStatements(null, loves, null, false).toList}.get.size shouldEqual 5
      self.read { con => con.getStatements(null, hates, null, false).toList}.get.size shouldEqual 2

    }
  }
}
