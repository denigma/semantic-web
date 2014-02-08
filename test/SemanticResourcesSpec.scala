import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.{SimpleResource, SimpleParser, SemanticResource, SemanticModel}
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.junit.runner.RunWith
import org.openrdf.model.impl.URIImpl
import org.openrdf.model.{Statement, URI, Resource}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication
class SemanticResourcesSpec extends Specification {
  val self = this

  val ant=new URIImpl("http://webintelligence.eu/ontology/actor/antonkulaga")
  val hev =new URIImpl("http://webintelligence.eu/ontology/actor/hevok")
  val il=new URIImpl("http://webintelligence.eu/ontology/actor/ILA")
  val de =new URIImpl("http://webintelligence.eu/ontology/actor/Denigma")
  val nick = new URIImpl("http://webintelligence.eu/test/Nick")
  val test = "http://webintelligence.eu/test/"

  "Semantic resources " should {

    "structure things well " in new WithApplication(){
      SG.platformParams.isEmpty should beTrue
      SG.db.parseFile("data/test/test_literals.ttl")
      val sr = new TestSimpleResource(nick)
      play.Logger.info(s"INFO WORS")
      sr.load(SG.db)

      sr.doubles.size shouldEqual 2
      sr.longs.size shouldEqual 3
      sr.dates.size shouldEqual 2
      sr.datetimes.size shouldEqual 2
      sr.otherliterals.size shouldEqual 4

      sr.doubles.get(new URIImpl(test+"double1")).get shouldEqual(1.1)
      sr.doubles.get(new URIImpl(test+"double2")).get shouldEqual(2.2)
      sr.longs.get(new URIImpl(test+"int1")).get shouldEqual(0)
      sr.longs.get(new URIImpl(test+"int2")).get shouldEqual(1)
      sr.longs.get(new URIImpl(test+"int3")).get shouldEqual(2)

      val dt1 = sr.datetimes.get(new URIImpl(test+"dateTime1")).get
      dt1.getYear shouldEqual(2002)
      dt1.getMinuteOfHour shouldEqual 0

      val dt2 = sr.datetimes.get(new URIImpl(test+"dateTime2")).get
      dt2.getYear shouldEqual(2012)
      dt2.getMinuteOfHour shouldEqual 30

      val d1 = sr.dates.get(new URIImpl(test+"date1")).get
      d1.getDayOfMonth shouldEqual(30)
      val d2 = sr.dates.get(new URIImpl(test+"date2")).get
      d2.getDayOfMonth shouldEqual(20)

    }

    "just parse statements with assignable handlers" in new WithApplication(){

      SG.platformParams.isEmpty should beTrue
      SG.db.parseFile("data/test/test_class.ttl")
    }
  }
}

class TestSimpleResource(url:Resource) extends SimpleResource(url){
  self=>
    object TestParser extends SimpleParser[this.type] with this.ModelParser

  parsers =  TestParser::parsers
}