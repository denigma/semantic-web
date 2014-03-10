package org.denigma.semantic.quering

import org.openrdf.model.impl.URIImpl
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.WithApplication

import org.denigma.semantic.test.LoveHater
import org.denigma.semantic.reading.selections.SelectResult
import org.denigma.semantic.controllers.sync.{SyncSimpleController, SyncJsController}
import scala.util.Try
import org.openrdf.query.TupleQueryResult
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.vocabulary.WI

/**
tests BigDataWrapper
  */
@RunWith(classOf[JUnitRunner])
class QueryModifiersSpec  extends Specification with LoveHater {
  val self = this

  class TestApp extends WithApplication with SyncJsController
  class SimpleTestApp extends WithApplication with SyncSimpleController


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

  "Query Modifiers" should {

    "write and read triples" in new TestApp() {
      self.addTestRels()
      val loves = new URIImpl("http://denigma.org/relations/resources/loves")

      self.getRel(loves).length shouldEqual(6)
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")

      self.getRel(hates).length shouldEqual(1)
    }


    "query with limits and offsets" in new TestApp {
      self.addTestRels()
      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = query(query)
      resFull.isSuccess shouldEqual(true)

      resFull.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(6)

      val resLimited= query(query,0,2)
      resLimited.isSuccess shouldEqual(true)
      resLimited.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(2)

      val resOffset= query(query,2,0)
      resOffset.isSuccess shouldEqual(true)
      resOffset.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(4)


    }


    "query with bindings" in new TestApp {
      self.addTestRels()
      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = query(query)
      resFull.isSuccess shouldEqual(true)

      resFull.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(6)

      val resBinded= bindedQuery(query,Map("o"->new URIImpl("http://denigma.org/actors/resources/RDF").stringValue()))
      resBinded.isSuccess shouldEqual(true)

      resBinded.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.length shouldEqual(1)

    }

    "query that must fail" in new SimpleTestApp {
      self.addTestRels()
//      val s1 =
//        """
//          | SELECT ?subject ?property ?object WHERE
//          | {
//          | ?subject ?property ?object .
//          | }
//          | LIMIT 50
//          | """
//          .stripMargin('|')
//
//
//      val q1: Try[TupleQueryResult] = this.select(s1)
//      q1.isSuccess should beTrue
//      q1.get.toList.size shouldEqual 7



      val sLove =
        """
          | SELECT ?subject ?property ?object WHERE
          | {
          | ?subject ?property ?object .
          | FILTER( STR(?property) "lov*") .
          | }
          | LIMIT 50
          | """
          .stripMargin('|')

      //TODO: figure out why sLove query leads to timeout

      val q2 = this.select(sLove)
       q2.isFailure should beTrue
      //q2.get.toList.size shouldEqual 6

    }


//    "build menu" in new WithApplication(){
//      SP.loadInitialData()
//      //    pg:Default_User_Menu a pg:Menu;
//      //    ui:child pg:My_Queries;
//      //    ui:child pg:My_Friends;
//      //    ui:child pg:My_Projects .
//
//      SP.db.parseFile("data/test/test_menu.ttl")
//
//    }


    //    "do text search well" in new WithApplication(){
    //      val loves ="<http://denigma.org/relations/resources/loves>"
    //      val immortality = "<http://denigma.org/actors/resources/Immortality>"
    //      self.addRel("Alexa","loves","Immortality")
    //      self.addRel("Alexey","loves","Immortality")
    //      self.addRel("Alexandr","loves","Immortality")
    //      self.addRel("Alexandra","loves","Immortality")
    //      self.addRel("Artem","loves","Immortality")
    //      self.addRel("Andrey","loves","Immortality")
    //      self.addRel("Anton","loves","Immortality")
    //      self.addRel("Anatoliy","loves","Immortality")
    //      self.addRel("Anderson","loves","Immortality")
    //      SP.db.lookup("Anton",loves,immortality).rows.size must beEqualTo(1)
    //      SP.db.lookup("Alexandr",loves,immortality).rows.size must beEqualTo(2)
    //      SP.db.lookup("Alexandra",loves,immortality).rows.size must beEqualTo(1)
    //      SP.db.lookup("to",loves,immortality).rows.size must beEqualTo(2)
    //      SP.db.lookup("ton",loves,immortality).rows.size must beEqualTo(1)
    //      SP.db.lookup("An",loves,immortality).rows.size must beEqualTo(4)
    //      }
  }


}