package org.denigma.semantic.shapes

import org.specs2.mutable._
import org.denigma.semantic.test.LoveHater
import org.denigma.semantic.controllers.{UpdateController, JsQueryController}
import org.denigma.semantic.WithSemanticPlugin
import scala.util._
import scala.concurrent.Future
import org.denigma.semantic.reading._
import org.scalax.semweb.shex._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary._
import org.denigma.semantic.reading.selections.SelectResult
import org.openrdf.model.impl.URIImpl

class DBActorSpec extends Specification with LoveHater {

  /*
  alias for "this"
   */
  self=>

  class WithTestApp extends WithSemanticPlugin with UpdateController


  object shape extends ShapeBuilder(IRI("http://myshape.com"))
  shape has FOAF.NAME of XSD.StringDatatypeIRI occurs ExactlyOne
  shape has FOAF.KNOWS oneOf (FOAF.PERSON,FOAF.Group) occurs Plus
  val res: Shape = shape.result
  assert(res.rule.isInstanceOf[AndRule])
  val and = res.rule.asInstanceOf[AndRule]
  assert(and.conjoints.size==2)
  val rules = and.conjoints.collect{case arc:ArcRule=>arc}


  "query with bindings" in new WithTestApp  {


    val aw: (Future[Try[QueryResultLike]]) => Try[QueryResultLike] = this.awaitRead[Try[QueryResultLike]] _

    this.update()

      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = aw {  this.query(query) }
      resFull.isSuccess shouldEqual(true)

      resFull.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(6)

      val resBinded= aw {  this.bindedQuery(query,Map("o"->new URIImpl("http://denigma.org/actors/resources/RDF").stringValue())) }
      resBinded.isSuccess shouldEqual(true)

      resBinded.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(1)

    }

  }

}