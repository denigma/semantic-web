package org.denigma.semantic.data

import org.denigma.semantic.SemanticPlatform
import org.openrdf.model.impl.URIImpl
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.WithApplication
import org.denigma.semantic.quering.QueryResult

import org.denigma.semantic.SP
import org.denigma.semantic.test.LoveHater


/**
tests BigDataWrapper
  */
@RunWith(classOf[JUnitRunner])
class SemanticStoreSpec  extends Specification with LoveHater {
  val self = this

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

  "SemanticStore BigData wrapper" should {




    "write and read triples" in new WithApplication() {


      self.addTestRels()

      val loves = new URIImpl("http://denigma.org/relations/resources/loves")

      self.getRel(loves).length shouldEqual(6)
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")

      self.getRel(hates).length shouldEqual(1)

    }

    "read files" in new WithApplication(){
      val q1 =
        """
          |PREFIX  de:   <http://denigma.org/resource/>
          |
          |SELECT  ?property ?object
          |WHERE
          |  { de:Genomic_Instability ?property ?object }
        """.stripMargin

      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFile("data/test/test_aging_ontology.ttl")
      val res = SP.query(q1)()
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.size shouldEqual(4)
    }

    "read initial data" in new WithApplication(){
      SP.loadInitialData()
      val q1 =
        """
          |PREFIX  de:   <http://denigma.org/resource/>
          |
          |SELECT  ?property ?object
          |WHERE
          |  { de:Genomic_Instability ?property ?object }
        """.stripMargin
      val res = SP.query(q1)()
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.size shouldEqual(4)
    }



  }


}