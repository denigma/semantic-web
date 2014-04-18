package org.denigma.semantic.console


/**
 * Provides cache console
 */
object CacheConsole {

  import org.denigma.semantic.controllers.sync.SyncUpdateController
  import org.denigma.sparql._

  import org.denigma.semantic.sesame._
  import org.denigma.rdf._

  import scala.collection.JavaConversions._
  import scala.concurrent.duration._
  import play.core._
  import akka.util.Timeout
  import org.openrdf.model.impl._
  import org.openrdf.query._

  import com.bigdata.rdf.sparql.ast._
  import com.bigdata.rdf.sail._

  //
  import org.denigma.semantic.test.LoveHater
  import org.denigma.semantic.platform.SP
  import org.denigma.semantic.controllers._


  import org.denigma.semantic.commons._
  import org.denigma.semantic.reading.selections._
  import com.bigdata.bop._

  implicit val writeTimeout:Timeout = Timeout(5 seconds)

  implicit val readTimeout:Timeout = Timeout(5 seconds)

  SP.platformParams = new StatementImpl(new URIImpl("http://hello.subject.world.com"),new URIImpl("http://hello.property.world.com"),new URIImpl("http://hello.object.world.com"))::SP.platformParams

  //needed for testing
  class ConsoleApp extends StaticApplication(new java.io.File(".")) with SyncUpdateController

  val i2: InsertQuery = InsertQuery {
    INSERT (
      DATA (
        Trip(
          IRI("http://denigma.org/actors/resources/Liz"),
          IRI("http://denigma.org/relations/resources/hates"),
          IRI("http://denigma.org/actors/resources/RDF")
        ),
        Trip(
          IRI("http://denigma.org/actors/resources/Daniel"),
          IRI("http://denigma.org/relations/resources/loves"),
          IRI("http://denigma.org/actors/resources/RDF")
        )
      )
    )
  }
  SP.cleanLocalDb()




}
