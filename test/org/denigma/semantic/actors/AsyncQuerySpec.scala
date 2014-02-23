package org.denigma.semantic.actors

import org.openrdf.model.impl.URIImpl
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.WithApplication

import org.denigma.semantic.SP
import org.denigma.semantic.quering.QueryResult
import org.denigma.semantic.controllers.SemanticController
import org.denigma.semantic.test.LoveHater

/**
tests BigDataWrapper
  */
//@RunWith(classOf[JUnitRunner])
//class AsyncQuerySpec  extends Specification with LoveHater {
//  val self = this
//
//  /*
// ads some test relationships
//  */
//  override def  addTestRels() = {
//    this.addRel("Daniel","loves","RDF")
//    this.addRel("Anton","hates","RDF")
//    this.addRel("Daniel","loves","Immortality")
//    this.addRel("Liz","loves","Immortality")
//    this.addRel("Anton","loves","Immortality")
//    this.addRel("Ilia","loves","Immortality")
//    this.addRel("Edouard","loves","Immortality")
//  }
//
//  "Asyn quering" should {
//
//
//
//    "query with limits and offsets" in new WithApplication with SemanticController  {
//      self.addTestRels()
//      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"
//
//      val resFull = SP.query(query)()
//      resFull.isSuccess shouldEqual(true)
//
//      resFull.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(6)
//
//      val resLimited= SP.paginatedQuery(query,0,2)
//      resLimited.isSuccess shouldEqual(true)
//      resLimited.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(2)
//
//      val resOffset= SP.paginatedQuery(query,2)
//      resOffset.isSuccess shouldEqual(true)
//      resOffset.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(4)
//
//
//    }
//
//
//    "query with bindings" in new WithApplication with SemanticController {
//      self.addTestRels()
//      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"
//
//      val resFull = SP.query(query)()
//      resFull.isSuccess shouldEqual(true)
//
//      resFull.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(6)
//
//      val resBinded= SP.queryWithBinding(query,"o"->new URIImpl("http://denigma.org/actors/resources/RDF"))
//      resBinded.isSuccess shouldEqual(true)
//
//      resBinded.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(1)
//
//    }
//  }
//
//
//}