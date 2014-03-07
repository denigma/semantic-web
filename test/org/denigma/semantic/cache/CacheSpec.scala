package org.denigma.semantic.cache

import org.specs2.mutable.Specification
import org.denigma.semantic.test.LoveHater
import play.api.test.WithApplication
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}

import org.specs2.mutable._
import play.api.test.WithApplication

import org.denigma.semantic.test.LoveHater
import scala.util.Try
import org.openrdf.query.TupleQueryResult
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.controllers.{SimpleQueryController, UpdateController}
import scala.concurrent.Future
import play.api.libs.concurrent.Akka
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql._
import org.denigma.semantic.sparql
import scala.collection.JavaConversions._
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading._

//class SparqlSpec extends Specification with LoveHater {
//
//  /*
//  alias for "this"
//   */
//  self=>
//
//  class WithTestApp extends WithApplication with SimpleQueryController with UpdateController
//
//
//  //type writing = BigdataSailRepositoryConnection=>Unit
//  /*
//  ads some test relationships
//   */
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
//
//  "Memcache" should {
//
//
//    "Cache inserted triplets" in new WithTestApp{
//      self.addTestRels()
//
//      val ins: InsertQuery = InsertQuery {
//        INSERT (
//          DATA (
//            Trip(
//              IRI("http://denigma.org/actors/resources/Anton"),
//              IRI("http://denigma.org/relations/resources/hates"),
//              IRI("http://denigma.org/actors/resources/Anton")
//            )
//          )
//        )
//      }
//
//      WI.
//
//
//
//    }
//  }
//}