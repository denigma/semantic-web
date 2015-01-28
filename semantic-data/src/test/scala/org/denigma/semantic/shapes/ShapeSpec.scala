package org.denigma.semantic.shapes

import org.denigma.semantic.WithSemanticPlugin
import org.denigma.semantic.controllers.{ShapeController, SimpleQueryController, UpdateController}
import org.denigma.semantic.platform.SP
import org.openrdf.query.TupleQueryResult
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary._
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex._
import org.scalax.semweb.sparql._
import org.specs2.mutable._

import scala.concurrent.Future
import scala.util._

class ShapeSpec extends Specification {

  /*
  alias for "this"
   */
  self=>

  //class WithTestApp extends WithSemanticPlugin with UpdateController

  class WithTestApp extends WithSemanticPlugin  with UpdateController with SimpleQueryController with ShapeController



  "Shapes" should{



  "get papers data by shape" in new WithTestApp  {

    SP.db.parseFileByName("data/test/paper.ttl")

    val aw = this.awaitRead[Try[TupleQueryResult]] _

    val de = IRI("http://denigma.org/resource/")

    val pmid = IRI("http://denigma.org/resource/Pubmed/")


    val authors = de / "authors"
    val ex = de / "excerpt"


    val pma = pmid / "17210671"

      val q = SELECT ( ?("a") ) WHERE Pat(?("a"), RDF.TYPE, de / "Article")

      //SP.lg.error(q.stringValue)

      val resFull: Try[TupleQueryResult] = aw {  this.select(q) }
      resFull.isSuccess shouldEqual(true)

      val sr = resFull.map(qr => qr.toSelectResults).get

      sr.rows.size shouldEqual 3




      val art = new ShapeBuilder(de / "Article_Shape")
      art has de / "journal" of XSD.StringDatatypeIRI occurs Plus
      art has de /"authors" occurs Plus
      art has de / "abstract" of XSD.StringDatatypeIRI
      art has  ex of XSD.StringDatatypeIRI

    art has RDFS.COMMENT occurs Star
      val paper = art.result

    SP.lg.error(paper.toString)

    val aws = this.awaitRead[Try[Set[PropertyModel]]] _

    val shapedResult: Future[Try[Set[PropertyModel]]] = this.selectWithShape(q,paper)

    val pt =  aws ( this.selectWithShape(q,paper) )
    pt.isSuccess should beTrue
    val papers = pt.get

    papers.size shouldEqual 3

    val pao = papers.find(p=>p.resource == pma)
    val pa = pao.get
    SP.lg.error(papers.toString())
    val exo = pa.properties.get(ex)
    exo.isDefined should beTrue
    val excerp = exo.get
    excerp.size shouldEqual 1
    excerp.head.stringValue.contains("Leigh syndrome associated with cytochrome") should beTrue

    }

    "get all shapes" in new WithTestApp {
      SP.db.parseFileByName("data/test/shapes.ttl")
      val res = IRI("http://gero.longevityalliance.org/Evidence_Shape")
      val shop = awaitRead(this.loadShape(res))
      shop.isSuccess shouldEqual true
      shop.get.id.asResource shouldEqual res

      /*val shapes = awaitRead(this.loadAllShapes())
      shapes.isSuccess shouldEqual true
      */
      //val ss= shapes.get
      //ss.size shouldEqual 1
      //val shape = ss.head
    }
  }

}