import com.bigdata.rdf.inf.RdfTypeRdfsResourceFilter
import com.bigdata.rdf.model.BigdataStatementImpl
import org.denigma.semantic.data.{QueryResult, QueryResultLike}
import org.denigma.semantic.SG
import org.denigma.semantic.SG._
import org.denigma.semantic.LoveHater
import org.openrdf.model.impl.{URIImpl, StatementImpl}
import org.openrdf.model.Statement
import org.openrdf.repository.RepositoryResult
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test.WithApplication
import play.Play
import scala.collection.immutable.List
import scala.collection.JavaConversions._
import org.openrdf.model.vocabulary._


/**
tests BigDataWrapper
 */
@RunWith(classOf[JUnitRunner])
class SemanticStoreSpec  extends Specification with LoveHater {
  val self = this

  /*
 ads some test relationships
  */
  override def  addTestRels() = {
    this.addRel("Daniel","loves","RDF")
    this.addRel("Anton","hates","RDF")
    this.addRel("Daniel","loves","Immortality")
    this.addRel("Liz","loves","Immortality")
    this.addRel("Anton","loves","Immortality")
    this.addRel("Ilia","loves","Immortality")
    this.addRel("Edouard","loves","Immortality")
  }

  "SemanticStore BigData wrapper" should {




    "write and read triples" in new WithApplication() {


      self.addTestRels()



      val loves = new URIImpl("http://denigma.org/relations/resources/loves")

      self.getRel(loves).length shouldEqual(6)
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")

      self.getRel(hates).length shouldEqual(1)

    }

    "query with limits and offsets" in new WithApplication() {
      self.addTestRels()
      val query = "SELECT ?s ?o WHERE { ?s <http://denigma.org/relations/resources/loves>  ?o }"

      val resFull = SG.db.query(query)
      resFull.isSuccess shouldEqual(true)

      resFull.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(6)

      val resLimited= SG.db.safeQuery(query,2,0)
      resLimited.isSuccess shouldEqual(true)
      resLimited.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(2)

      val resOffset= SG.db.safeQuery(query,0,5)
      resLimited.isSuccess shouldEqual(true)
      resLimited.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.length shouldEqual(2)


    }


    "read files" in new WithApplication(){
      val q1 =
        """
          |PREFIX  de:   <http://denigma.org/resource/>
          |
          |SELECT  ?property ?object
          |WHERE
          |  { de:Genomic_Instability ?property ?object }
        """.stripMargin

      SG.platformParams.isEmpty should beTrue
      SG.db.parseFile("data/test/test_aging_ontology.ttl")
      val res = SG.db.query(q1)
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.size shouldEqual(4)
    }

    "read initial data" in new WithApplication(){
      SG.loadInitialData()
      val q1 =
        """
          |PREFIX  de:   <http://denigma.org/resource/>
          |
          |SELECT  ?property ?object
          |WHERE
          |  { de:Genomic_Instability ?property ?object }
        """.stripMargin
      val res = SG.db.query(q1)
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.size shouldEqual(4)
    }

    "build menu" in new WithApplication(){
      SG.loadInitialData()
      //    pg:Default_User_Menu a pg:Menu;
      //    ui:child pg:My_Queries;
      //    ui:child pg:My_Friends;
      //    ui:child pg:My_Projects .

      val q1 =
        """
          |PREFIX  de:   <http://denigma.org/resource/>
          |PREFIX  ui:   <http://uispin.org/ui#>
          |PREFIX  pg:   <http://webintelligence.eu/page/>
          |
          |SELECT  ?object
          |WHERE
          |  { pg:Default_User_Menu ui:child ?object }
          |
        """.stripMargin
      SG.db.parseFile("data/test/test_menu.ttl")
      val res = SG.db.query(q1)
      res.isSuccess should beTrue
      res.map(qr=>qr.asInstanceOf[QueryResult]).get.bindings.size shouldEqual(3)
    }


//    "do text search well" in new WithApplication(){
//      val loves ="<http://denigma.org/relations/resources/loves>"
//      val immortality = "<http://denigma.org/actors/resources/Immortality>"
//      self.addRel("Alexa","loves","Immortality")
//      self.addRel("Alexey","loves","Immortality")
//      self.addRel("Alexandr","loves","Immortality")
//      self.addRel("Alexandra","loves","Immortality")
//      self.addRel("Artem","loves","Immortality")
//      self.addRel("Andrey","loves","Immortality")
//      self.addRel("Anton","loves","Immortality")
//      self.addRel("Anatoliy","loves","Immortality")
//      self.addRel("Anderson","loves","Immortality")
//      SG.db.lookup("Anton",loves,immortality).rows.size must beEqualTo(1)
//      SG.db.lookup("Alexandr",loves,immortality).rows.size must beEqualTo(2)
//      SG.db.lookup("Alexandra",loves,immortality).rows.size must beEqualTo(1)
//      SG.db.lookup("to",loves,immortality).rows.size must beEqualTo(2)
//      SG.db.lookup("ton",loves,immortality).rows.size must beEqualTo(1)
//      SG.db.lookup("An",loves,immortality).rows.size must beEqualTo(4)
//      }
  }


}