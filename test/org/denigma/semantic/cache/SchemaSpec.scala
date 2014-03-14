package org.denigma.semantic.cache

import play.api.test.WithApplication
import org.denigma.semantic.platform.SP
import org.denigma.semantic.classes.{SemanticProperty, SemanticClass}
import org.openrdf.model.vocabulary.RDFS
import org.specs2.mutable.Specification
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}
import org.denigma.semantic.model.IRI

/**
 * Specification to test inmemory graph cache
 */
//class SchemaSpec extends Specification
//{

//  /*
//  alias for "this"
//   */
//  self=>
//  skipAllIf(true) //TODO: change it
//
//  class WithTestApp extends WithApplication with SimpleQueryController with UpdateController
//
//  val ant=IRI("http://webintelligence.eu/ontology/actor/antonkulaga")
//  val hev =IRI("http://webintelligence.eu/ontology/actor/hevok")
//  val il=IRI("http://webintelligence.eu/ontology/actor/ILA")
//  val de =IRI("http://webintelligence.eu/ontology/actor/Denigma")
//  val nick = IRI("http://webintelligence.eu/test/Nick")
//  val test = "http://webintelligence.eu/test/"
//
//  val testObject = IRI(test+"TestObject")
//  val testClass = IRI(test+"TestClass")
//  val test2Class = IRI(test+"Test2Class")
//  val test21Class = IRI(test+"Test21Class")
//  val test3Class = IRI(test+"Test3Class")
//
//
//  val prop1 = IRI(test+"prop1")
//  val prop2 = IRI(test+"prop2")
//  val prop3 = IRI(test+"prop3")
//
//
//  "extract class hirercy well" in new WithTestApp(){
//    //SP.platformParams.isEmpty should beTrue
//    SP.db.parseFileByName("data/test/test_class.ttl")
//    val sc = new SemanticClass(testClass)
//    sc.load(SP.db)
//    sc.types.size shouldEqual(1)
//    sc.types.get(RDFS.CLASS).isDefined shouldEqual true
//
//    sc.parentClasses.size shouldEqual(2)
//    sc.subClasses.size.shouldEqual(1)
//
//    val t2o = sc.parentClasses.get(test2Class)
//    t2o.isDefined should beTrue
//
//    val t2 = t2o.get
//    t2.subClasses.size shouldEqual 1
//
//    t2.parentClasses.size shouldEqual 1
//    val t3o = t2.parentClasses.get(test3Class)
//    t3o.isDefined should beTrue
//    val t3 = t3o.get
//    t3.url shouldEqual test3Class
//    //t3.parentClasses.size.shouldEqual(0)
//
//
//    // sc
//  }

//  "have properties" in new WithTestApp(){
//    //SP.platformParams.isEmpty should beTrue
//    SP.db.parseFileByName("data/test/test_class.ttl")
//    val sc = new SemanticClass(testClass)
//    sc.load(SP.db)
//    val p1o = sc.domainOf.get(prop1)
//    p1o.isDefined should beTrue
//    val t2 = sc.parentClasses(test2Class)
//    val t3o = t2.parentClasses.get(test3Class)
//    t3o.isDefined should beTrue
//    val t3 = t3o.get
//    t3.url shouldEqual test3Class
//
//    val p1 = p1o.get
//    val p2o = p1.parentProperties.get(prop2)
//    val p2: SemanticProperty = p2o.get
//    p2o.isDefined should beTrue

//      p1.domains.get(sc.url).isDefined should beTrue
//      p1.ranges.get(sc.url).isDefined should beFalse
//      p1.ranges.get(t2.url).isDefined should beTrue
//      p2.subProperties.get(p1.url).isDefined should beTrue
//      p2.domains.get(sc.url).isDefined should beFalse
//      p2.domains.get(t2.url).isDefined should beTrue
//      t2.domainOf.get(p2.url).isDefined should beTrue
//
//
//  }
//}
