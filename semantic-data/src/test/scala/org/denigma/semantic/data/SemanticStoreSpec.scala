package org.denigma.semantic.data

import org.denigma.semantic.reading.SelectResult
import org.openrdf.model.impl.URIImpl
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.{Helpers, FakeApplication, WithApplication}

import org.denigma.semantic.test.LoveHater
import org.denigma.semantic.platform.SP
import org.denigma.semantic.controllers.sync.{SyncUpdateController, SyncJsController}
import play.api.Configuration
import org.specs2.specification.Scope
import org.specs2.execute.{Result, AsResult}
import org.denigma.semantic.WithSemanticPlugin


/**
tests BigDataWrapper
  */
@RunWith(classOf[JUnitRunner])
class SemanticStoreSpec  extends Specification with LoveHater {
  self=>

  /*
  just a class that adds some features to TestApp
   */
  class TestApp extends  WithSemanticPlugin with SyncJsController with SyncUpdateController
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


    "write and read triples" in new TestApp() {


      self.addTestRels()

      val loves = new URIImpl("http://denigma.org/relations/resources/loves")

      self.getRel(loves).length shouldEqual(6)
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")

      self.getRel(hates).length shouldEqual(1)

    }

    "load files" in new TestApp(){
      SP.platformConfig.loadInitial should beFalse
      val q1 =
        """
          |PREFIX  de:   <http://denigma.org/resource/>
          |
          |SELECT  ?property ?object
          |WHERE
          |  { de:Genomic_Instability ?property ?object }
        """.stripMargin

      //SP.platform
      // Params.isEmpty should beTrue
      this.parseFileByName("data/test/test_aging_ontology.ttl")
      val res = query(q1)
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.size shouldEqual(4)
    }

    "loaf files for initial data" in new TestApp(){
      SP.platformConfig.loadInitial should beFalse
      SP.platformParams.isEmpty should beTrue
      val files: List[Configuration] = SP.platformConfig.filesConf
      files.isEmpty should beFalse

      lg.info("starting loading initial data")
      SP.loadInitialData()
      //this.parseFileByName("data/denigma/Aging_ontology.ttl")
      //this.parseFileByName("data/test/test_aging_ontology.ttl")

      val q1 =
        """
          |PREFIX  de: <http://denigma.org/resource/>
          |
          |SELECT  ?property ?object
          |WHERE
          |  { de:Genomic_Instability ?property ?object }
        """.stripMargin

      val res = query(q1)
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[SelectResult]).get.bindings.size shouldEqual(4)
    }







  }


}