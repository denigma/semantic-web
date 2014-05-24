package org.denigma.semantic.classes

import org.specs2.mutable.Specification
import play.api.test.WithApplication
import org.openrdf.model.vocabulary._
import org.denigma.semantic.platform.SP
import org.denigma.semantic.controllers.sync.SyncJsController
import org.scalax.semweb.sesame._

import org.scalax.semweb.rdf.IRI
import org.denigma.semantic.WithSemanticPlugin


class SemanticResourcesSpec extends Specification {
  val self = this

  skipAllIf(true) //TODO: throw it away


  class TestApp extends WithSemanticPlugin with SyncJsController

  val ant=IRI("http://webintelligence.eu/ontology/actor/antonkulaga")
  val hev =IRI("http://webintelligence.eu/ontology/actor/hevok")
  val il=IRI("http://webintelligence.eu/ontology/actor/ILA")
  val de =IRI("http://webintelligence.eu/ontology/actor/Denigma")
  val nick = IRI("http://webintelligence.eu/test/Nick")
  val test = "http://webintelligence.eu/test/"

  val testObject = IRI(test+"TestObject")
  val testClass = IRI(test+"TestClass")
  val test2Class = IRI(test+"Test2Class")
  val test21Class = IRI(test+"Test21Class")
  val test3Class = IRI(test+"Test3Class")


  val prop1 = IRI(test+"prop1")
  val prop2 = IRI(test+"prop2")
  val prop3 = IRI(test+"prop3")

  "SimpleResource" should {

    "extract literals well" in new TestApp{
      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFileByName("data/test/test_literals.ttl")
      val sr = new SimpleResource(nick)
      play.Logger.info(s"INFO WORS")
      sr.load(SP.db)

      sr.doubles.size shouldEqual 2
      sr.longs.size shouldEqual 3
      sr.dates.size shouldEqual 2
      sr.dateTimes.size shouldEqual 2
      sr.otherliterals.size shouldEqual 4

      sr.doubles.get(IRI(test+"double1")).get.head shouldEqual(1.1)
      sr.doubles.get(IRI(test+"double2")).get.head shouldEqual(2.2)
      sr.longs.get(IRI(test+"int1")).get.head shouldEqual(0)
      sr.longs.get(IRI(test+"int2")).get.head shouldEqual(1)
      sr.longs.get(IRI(test+"int3")).get.head shouldEqual(2)

      val dt1 = sr.dateTimes.get(IRI(test+"dateTime1")).get.head
      dt1.getYear shouldEqual(2002)
      dt1.getMinuteOfHour shouldEqual 0

      val dt2 = sr.dateTimes.get(IRI(test+"dateTime2")).get.head
      dt2.getYear shouldEqual(2012)
      dt2.getMinuteOfHour shouldEqual 30

      val d1 = sr.dates.get(IRI(test+"date1")).get.head
      d1.getDayOfMonth shouldEqual(30)
      val d2 = sr.dates.get(IRI(test+"date2")).get.head
      d2.getDayOfMonth shouldEqual(20)

      sr.outgoingResources.size.shouldEqual(2)
      sr.outgoingResources.get(IRI("http://www.w3.org/ns/org#memberOf")).get.size.shouldEqual(2)

    }


  }

  "SemanticClass" should{
    "extract class hirercy well" in new WithApplication(){
      //SP.platformParams.isEmpty should beTrue
      SP.db.parseFileByName("data/test/test_class.ttl")
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
      SP.db.parseFileByName("data/test/test_class.ttl")
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


    }.pendingUntilFixed("message about the issue")
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
