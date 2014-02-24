package org.denigma.semantic.classes

import org.openrdf.model.impl.URIImpl
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import org.openrdf.model.vocabulary._
import org.denigma.semantic.platform.SP

class SemanticResourcesSpec extends Specification {
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

  "SimpleResource" should {

    "extract literals well" in new WithApplication(){
      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFile("data/test/test_literals.ttl")
      val sr = new SimpleResource(nick)
      play.Logger.info(s"INFO WORS")
      sr.load(SP.db)

      sr.doubles.size shouldEqual 2
      sr.longs.size shouldEqual 3
      sr.dates.size shouldEqual 2
      sr.dateTimes.size shouldEqual 2
      sr.otherliterals.size shouldEqual 4

      sr.doubles.get(new URIImpl(test+"double1")).get.head shouldEqual(1.1)
      sr.doubles.get(new URIImpl(test+"double2")).get.head shouldEqual(2.2)
      sr.longs.get(new URIImpl(test+"int1")).get.head shouldEqual(0)
      sr.longs.get(new URIImpl(test+"int2")).get.head shouldEqual(1)
      sr.longs.get(new URIImpl(test+"int3")).get.head shouldEqual(2)

      val dt1 = sr.dateTimes.get(new URIImpl(test+"dateTime1")).get.head
      dt1.getYear shouldEqual(2002)
      dt1.getMinuteOfHour shouldEqual 0

      val dt2 = sr.dateTimes.get(new URIImpl(test+"dateTime2")).get.head
      dt2.getYear shouldEqual(2012)
      dt2.getMinuteOfHour shouldEqual 30

      val d1 = sr.dates.get(new URIImpl(test+"date1")).get.head
      d1.getDayOfMonth shouldEqual(30)
      val d2 = sr.dates.get(new URIImpl(test+"date2")).get.head
      d2.getDayOfMonth shouldEqual(20)

      sr.outgoingResources.size.shouldEqual(2)
      sr.outgoingResources.get(new URIImpl("http://www.w3.org/ns/org#memberOf")).get.size.shouldEqual(2)

    }


  }

  "SemanticClass" should{
    "extract class hirercy well" in new WithApplication(){
      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFile("data/test/test_class.ttl")
      val sc = new SemanticClass(testClass)
      sc.load(SP.db)
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


     // sc
    }

    "have properties" in new WithApplication(){
      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFile("data/test/test_class.ttl")
      val sc = new SemanticClass(testClass)
      sc.load(SP.db)
      val p1o = sc.domainOf.get(prop1)
      p1o.isDefined should beTrue
      val t2 = sc.parentClasses(test2Class)
      val t3o = t2.parentClasses.get(test3Class)
      t3o.isDefined should beTrue
      val t3 = t3o.get
      t3.url shouldEqual test3Class

      val p1 = p1o.get
      val p2o = p1.parentProperties.get(prop2)
      val p2: SemanticProperty = p2o.get
      p2o.isDefined should beTrue

//      p1.domains.get(sc.url).isDefined should beTrue
//      p1.ranges.get(sc.url).isDefined should beFalse
//      p1.ranges.get(t2.url).isDefined should beTrue
//      p2.subProperties.get(p1.url).isDefined should beTrue
//      p2.domains.get(sc.url).isDefined should beFalse
//      p2.domains.get(t2.url).isDefined should beTrue
//      t2.domainOf.get(p2.url).isDefined should beTrue


    }
  }

//  "Semantic Property" should{
//    "be provided for each class" in new WithApplication(){
//      SP.platformParams.isEmpty should beTrue
//      SP.db.parseFile("data/test/test_class.ttl")
//      val sc = new SemanticClass(testClass)
//      sc.get
//
//
//      sc
//    }
//  }
}
