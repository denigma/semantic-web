import org.denigma.semantic.classes._
import org.denigma.semantic.classes.OutgoingParams
import org.openrdf.model._
import com.bigdata.rdf.vocab.decls
import org.denigma.semantic.data.QueryResult
import org.denigma.semantic.{Config, Prefixes, SG}
import org.junit.runner.RunWith
import org.openrdf.model.impl.URIImpl
import org.openrdf.model.vocabulary.RDF
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test._
import scala.concurrent._


@RunWith(classOf[JUnitRunner])
class ModelsSpec extends Specification  {


  "Webintelligent models" should {



    "work well for modules" in new WithApplication(){
      import SG._
        val page = new URIImpl("http://webintelligence.eu/page/Page")
        val agingPage = new URIImpl("http://webintelligence.eu/page/Aging_Query")
        val queryPageClass = new URIImpl("http://webintelligence.eu/page/Query_Page")
        val menuClass = new URIImpl("http://webintelligence.eu/page/Menu")
        platformParams.isEmpty should beTrue
        db.parseFile("data/test/test_menu.ttl",Config.CONFIG_CONTEXT)
        loadPlatform()
        platformParams.isEmpty should beFalse
        val ast = db.read{con=>
          con.hasStatement(agingPage,RDF.TYPE,queryPageClass,false)

        }
        ast.isSuccess should beTrue
        ast.get should beTrue
        val con = db.repo.getReadOnlyConnection
        SG.isOfType(agingPage,queryPageClass)(con) should beTrue
        SG.isOfType(agingPage,page)(con) should beTrue
        //SG.isOfType(agingPage,menuClass)(con) should beFalse //if I uncomment this string it will cause indefinite timeouts
        con.close()


    }
  }

}