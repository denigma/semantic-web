import com.bigdata.rdf.inf.RdfTypeRdfsResourceFilter
import com.bigdata.rdf.model.BigdataStatementImpl
import org.denigma.semantic.data.{SG, LoveHater}
import org.denigma.semantic.data.SG._
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
class BigDataSpec  extends Specification with LoveHater {
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

  "BigData wrapper" should {


    "write and read triples" in new WithApplication() {


      self.addTestRels()



      val loves = new URIImpl("http://denigma.org/relations/resources/loves")

      self.getRel(loves).length shouldEqual(6)
      val hates = new URIImpl("http://denigma.org/relations/resources/hates")

      self.getRel(hates).length shouldEqual(1)

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