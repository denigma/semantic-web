package org.denigma.semantic.data

import org.denigma.semantic.classes._
import org.denigma.semantic.SemanticPlatform
import org.openrdf.model.impl.URIImpl
import org.specs2.mutable.Specification
import play.api.cache.Cache
import play.api.test.WithApplication
import org.openrdf.model.vocabulary._

import org.denigma.semantic.SP

class CacheSpec extends Specification {
  val self = this

  val ant=new URIImpl("http://webintelligence.eu/ontology/actor/antonkulaga")
  val hev =new URIImpl("http://webintelligence.eu/ontology/actor/hevok")
  val il=new URIImpl("http://webintelligence.eu/ontology/actor/ILA")
  val de =new URIImpl("http://webintelligence.eu/ontology/actor/Denigma")
  val nick = new URIImpl("http://webintelligence.eu/test/Nick")
  val test = "http://webintelligence.eu/test/"

  val testObject = new URIImpl(test+"TestObject")
  val testClass = new URIImpl(test+"TestClass")
  val test2Class = new URIImpl(test+"Test2Class")
  val test21Class = new URIImpl(test+"Test21Class")
  val test3Class = new URIImpl(test+"Test3Class")


  val prop1 = new URIImpl(test+"prop1")
  val prop2 = new URIImpl(test+"prop2")
  val prop3 = new URIImpl(test+"prop3")

  "Cache" should {

    "extract class hirercy well" in new WithApplication(){
      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFile("data/test/test_class.ttl")
      val sc1 = new SemanticClass(testClass)
      sc1.load(SP.db)
      Cache.set(sc1.url.stringValue(),sc1)

      val sco = Cache.getAs[SemanticClass](sc1.url.stringValue())
      sco.isDefined should beTrue
      val sc = sco.get


      sc.types.size shouldEqual(1)
      sc.types.get(RDFS.CLASS).isDefined shouldEqual true

      sc.parentClasses.size shouldEqual(2)
      sc.subClasses.size.shouldEqual(1)

      val t2o = sc.parentClasses.get(test2Class)
      t2o.isDefined should beTrue

      val t2 = t2o.get
      t2.subClasses.size shouldEqual 1

      t2.parentClasses.size shouldEqual 1
      val t3o = t2.parentClasses.get(test3Class)
      t3o.isDefined should beTrue
      val t3 = t3o.get
      t3.url shouldEqual test3Class
      //t3.parentClasses.size.shouldEqual(0)


      sc
    }

  }
}
