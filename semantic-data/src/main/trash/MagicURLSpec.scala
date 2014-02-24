package org.denigma.semantic.data

import org.denigma.semantic.SemanticPlatform
import org.openrdf.model.impl.URIImpl
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.WithApplication

import org.denigma.semantic.test.LoveHater
import org.denigma.semantic.platform.SP


/**
tests BigDataWrapper
  */
@RunWith(classOf[JUnitRunner])
class MagicURLSpec  extends Specification with LoveHater {
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

  "Magic URL" should {



    "provide outgoing nodes" in new WithApplication() {
      import SP._

      self.addTestRels()




      val loves = new URIImpl("http://denigma.org/relations/resources/loves")
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")
      val Daniel = new URIImpl("http://denigma.org/actors/resources/Daniel")
      val Anton = new URIImpl("http://denigma.org/actors/resources/Anton")
      val Liz = new URIImpl("http://denigma.org/actors/resources/Liz")

      self.getRel(loves).length shouldEqual(6)
      self.getRel(hates).length shouldEqual(1)

      (Daniel ~> loves size) shouldEqual 2
      (Daniel ~> hates size) shouldEqual 0
      (Daniel ~> (false) size) shouldEqual 2

      (Anton ~> loves size) shouldEqual 1
      (Anton ~> hates size) shouldEqual 1
      (Anton ~> (false) size) shouldEqual 2

    }

    "provide incoming nodes" in new WithApplication() {
      import SP._

      self.addTestRels()




      val loves = new URIImpl("http://denigma.org/relations/resources/loves")
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")
      val Daniel = new URIImpl("http://denigma.org/actors/resources/Daniel")
      val Anton = new URIImpl("http://denigma.org/actors/resources/Anton")
      val Liz = new URIImpl("http://denigma.org/actors/resources/Liz")



      val RDF = new URIImpl("http://denigma.org/actors/resources/RDF")
      val Immortality = new URIImpl("http://denigma.org/actors/resources/Immortality")

      (RDF ~> loves size) shouldEqual 0
      (RDF <~ loves size) shouldEqual 1

      (RDF ~> hates size) shouldEqual 0
      (RDF <~ hates size) shouldEqual 1
      (RDF ~>(false) size) shouldEqual 0
      (RDF <~(false) size) shouldEqual 2

      (Immortality ~> loves size) shouldEqual 0
      (Immortality <~ loves size) shouldEqual 5
      (Immortality <~ (false) size) shouldEqual 5


    }
  }
}