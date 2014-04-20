package org.denigma.semantic.console


/*
just a code to copy-paste to play console
*/
object SparqlConsole {
  import org.denigma.semantic.sesame._
  import org.denigma.rdf.sparql.{Pat, Br, SELECT}

  import org.openrdf.model._
  import org.openrdf.model.impl._

  import play.api.test.WithApplication
  import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}
  import scala.concurrent.Future
  import scala.util.Try
  import play.api.libs.concurrent.Akka
  import org.denigma.rdf.sparql._

  import scala.collection.JavaConversions._
  import scala.concurrent.duration._
  import play.core._
  import akka.util.Timeout
  import org.openrdf.model.impl._
  import org.openrdf.query._

  import com.bigdata.rdf.sparql.ast._
  import com.bigdata.rdf.sail._
  import org.denigma.rdf.model.IRI

  //
  import org.denigma.semantic.test.LoveHater
  import org.denigma.semantic.platform.SP
  import org.denigma.semantic.controllers._


  import org.denigma.semantic.commons._
  import org.denigma.semantic.reading.selections._

  implicit val writeTimeout:Timeout = Timeout(5 seconds)

  implicit val readTimeout:Timeout = Timeout(5 seconds)

   SP.platformParams = new StatementImpl(new URIImpl("http://hello.subject.world.com"),new URIImpl("http://hello.property.world.com"),new URIImpl("http://hello.object.world.com"))::SP.platformParams

  //needed for testing
  class ConsoleApp extends StaticApplication(new java.io.File(".")) with SimpleQueryController  with LoveHater

  val app: ConsoleApp = new ConsoleApp

  import app._
  SP.cleanLocalDb()

  addTestRels()
  val aw: (Future[Try[TupleQueryResult]]) => Try[TupleQueryResult] = awaitRead[Try[TupleQueryResult]] _

  implicit val sys = Akka.system(app.application)

  val q: String = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

  val resFull = aw { select(q) }

  val q2= SELECT( ?("s"), ?("o")) WHERE {
    Br { Pat( ?("s"), IRI("http://denigma.org/relations/resources/loves"), ?("o") ) }
  }

  val resFull2 = aw { select(q2) }

  val resLimited= aw { select(q,0,2) }

}
