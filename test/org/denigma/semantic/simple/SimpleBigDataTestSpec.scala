package org.denigma.semantic.simple

import scala.util.Try
import org.openrdf.query.{TupleQueryResult, BindingSet}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import scala.collection.immutable.{Map, List}
import org.openrdf.model.{Statement, Value}
import collection.JavaConversions._
import org.openrdf.repository.RepositoryResult

/**
 * converts results in more convenient type
 */
object ImplicitConversions {


  /**
   * turns RepositoryResult into scala iterator and adds some other sueful methods
   * @param results
   */
  implicit class TupleResult(results: TupleQueryResult)  extends Iterator[BindingSet]
  {

    lazy val vars:List[String] = results.getBindingNames.toList

    def binding2Map(b:BindingSet): Map[String, Value] = b.iterator().map(v=>v.getName->v.getValue).toMap

    lazy val toListMap: List[Map[String, Value]] = this.map(v=>binding2Map(v)).toList


    override def next(): BindingSet = results.next()

    override def hasNext: Boolean = results.hasNext
  }

  /**
   * turns RepositoryResult into scala iterator
   * @param results
   */
  implicit class StatementsResult(results:RepositoryResult[Statement]) extends Iterator[Statement]{

    override def next(): Statement = results.next()

    override def hasNext: Boolean = results.hasNext
  }


}

/**
tests, if you want to run only this spec, type:
*/
@RunWith(classOf[JUnitRunner])
class SimpleBigDataTestSpec  extends Specification with SimpleTestData {
  self=>

  import ImplicitConversions._ //will add additional methods to QueryResults


  "Simple bigdata" should {

    "read something successfully" in {

      val db = BigData(true) //cleaning the files and initializing the database

      self.addTestData(db) //add test data ( see SimpleTestData )


      val tryLove:Try[RepositoryResult[Statement]]= db.read{con=>
        con.getStatements(null,self.loves,null,true)
      }
      tryLove.isSuccess should beTrue

      val resLove = tryLove.get.toList
      resLove.size shouldEqual 6

      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"
      val queryLove= db.select(query)
      queryLove.isSuccess should beTrue
      queryLove.get.toList.size shouldEqual 6

      db.shutDown() // shutting down


      true //if test passed


    }


    "Failure on wrong query" in {

      val db = BigData(true) //cleaning the files and initializing the database
      self.addTestData(db) //add test data ( see SimpleTestData )



      val wrongQuery =
      """
        | SELECT ?subject ?property ?object WHERE
        | {
        | ?subject ?property ?object .
        | FILTER( STR(?property) "lov*") .
        | }
        | LIMIT 50
        | """
        .stripMargin('|')

      //UNCOMMENT FOLLOWING LINES TO SEE TIMEOUTS
     val queryFreeze= db.select(wrongQuery)
     queryFreeze.isFailure should beTrue
     db.shutDown() // shutting down

      true  //if test passed

    }
  }
}
