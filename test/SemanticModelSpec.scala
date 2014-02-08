import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.SemanticModel
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.junit.runner.RunWith
import org.openrdf.model.impl.URIImpl
import org.openrdf.model.{Statement, URI, Resource}
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication


@RunWith(classOf[JUnitRunner])
class SemanticModelSpec  extends Specification {
  val self = this

  val ant=new URIImpl("http://webintelligence.eu/ontology/actor/antonkulaga")
  val hev =new URIImpl("http://webintelligence.eu/ontology/actor/hevok")
  val il=new URIImpl("http://webintelligence.eu/ontology/actor/ILA")
  val de =new URIImpl("http://webintelligence.eu/ontology/actor/Denigma")

  "SemanticModel" should {

    "just parse statements with assignable handlers" in new WithApplication(){

      SG.platformParams.isEmpty should beTrue
      SG.db.parseFile("data/test/test_user.ttl")

      """
        |actor:antonkulaga a actor:User;
        |    foaf:gender "male";
        |    foaf:givenName "Anton";
        |    foaf:familyName "Kulaga";
        |    foaf:skypeID "anton_y_k";
        |    org:memberOf actor:ILA;
        |    org:memberOf actor:Denigma .
        |
        |actor:hevok a actor:User;
        |    foaf:gender "male";
        |    foaf:givenName "Daniel";
        |    foaf:familyName "Wuttke";
        |    org:memberOf actor:ILA;
        |    org:memberOf actor:Denigma .
        |
        |
        |
        |actor:ILA a foaf:Organization ;
        |    org:hasMember actor:hevok;
        |    org:hasMember actor:antonkulaga;
        |    dcterms:description "International longevity alliance" .
        |
        |actor:Denigma a foaf:Organization;
        |    org:subOrganizationOf actor:ILA;
        |    dcterms:description "Digital dechiper machine";
        |    org:hasMember actor:hevok;
        |    org:hasMember actor:antonkulaga .
        |
      """.stripMargin

      val anton: TestStringModel = new TestStringModel(self.ant)
      anton.strings.size shouldEqual(0)
      anton.load(SG.db)
      anton.strings.size shouldEqual(4)

      val hevok: TestStringModel = new TestStringModel(self.hev)
      hevok.strings.size shouldEqual(0)
      hevok.load(SG.db)
      hevok.strings.size shouldEqual(3)

      val ila: TestStringModel = new TestStringModel(self.il)
      ila.load(SG.db)
      ila.strings.size shouldEqual(1)

      val denigma: TestStringModel = new TestStringModel(self.de)
      denigma.load(SG.db)
      denigma.strings.size shouldEqual(1)


    }

    "parse complex structures(organizations)" in new WithApplication(){

      SG.platformParams.isEmpty should beTrue
      SG.db.parseFile("data/test/test_user.ttl")


      val denigma: TestOrganizationModel = new TestOrganizationModel(self.de)
      denigma.load(SG.db)
      denigma.strings.size shouldEqual(1)
      denigma.hasMember.size shouldEqual(2)
      denigma.hasMember.contains(self.hev) should beTrue
      denigma.hasMember.contains(self.ant) should beTrue

      denigma.hasMember.get(ant).get.strings.size shouldEqual(4)
      denigma.hasMember.get(hev).get.strings.size shouldEqual(3)

    }

    "parse complex structures(users)" in new WithApplication(){

      SG.platformParams.isEmpty should beTrue
      SG.db.parseFile("data/test/test_user.ttl")

      val anton= new TestUserModel(self.ant)
      anton.strings.size shouldEqual(0)
      anton.load(SG.db)
      anton.strings.size shouldEqual(4)
      anton.memberOf.size.shouldEqual(2)

      val hevok = new TestUserModel(self.hev)
      hevok.strings.size shouldEqual(0)
      hevok.load(SG.db)
      hevok.strings.size shouldEqual(3)
      hevok.memberOf.size.shouldEqual(2)

      hevok.memberOf.contains(de) should beTrue

      val denigma: TestOrganizationModel = hevok.memberOf.get(de).get
      denigma.load(SG.db)
      denigma.strings.size shouldEqual(1)
      denigma.hasMember.size shouldEqual(2)
      denigma.hasMember.contains(self.hev) should beTrue
      denigma.hasMember.contains(self.ant) should beTrue

      denigma.hasMember.get(ant).get.strings.size shouldEqual(4)
      denigma.hasMember.get(hev).get.strings.size shouldEqual(3)

    }
  }

}
import org.openrdf.model.vocabulary
import com.bigdata.rdf.vocab.decls

class TestUserModel(url:URI) extends TestStringModel(url)
{
  self=>
  var memberOf:Map[URI,TestOrganizationModel] = Map.empty

  object MemberOfParser extends self.ModelParser{
    override def apply(model:self.type,con:BigdataSailRepositoryConnection, st:Statement,path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Boolean = st.getObject match {
      case o:org.openrdf.model.URI if path.contains(o) || memberOf.contains(o) =>true

      case o:org.openrdf.model.URI if con.hasStatement(o,vocabulary.RDF.TYPE,decls.FOAFVocabularyDecl.Organization,true)=>
        val organ =new TestOrganizationModel(o)
        self.memberOf = self.memberOf+(o->organ)
        organ.hasMember = organ.hasMember + (self.url->self)
        organ.load(con,path)
        true
      case r=>
        false
    }
  }
  this.parsers = MemberOfParser::self.parsers

}

class TestOrganizationModel(url:URI) extends TestStringModel(url){
  self=>

  val user = new URIImpl("http://webintelligence.eu/ontology/actor/User")

  var hasMember:Map[URI,TestUserModel]  = Map.empty


  object HasMemberParser extends self.ModelParser{
    override def apply(model:self.type,con:BigdataSailRepositoryConnection, st:Statement,path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Boolean = st.getObject match {
      case o:org.openrdf.model.URI if path.contains(o) || hasMember.contains(o) =>true

      case o:org.openrdf.model.URI if con.hasStatement(o,vocabulary.RDF.TYPE,user,true)=>
        val um = new TestUserModel(o)

        self.hasMember = self.hasMember+(o->um)
        um.memberOf = um.memberOf+(self.url->self)
        um.load(con,path)
        true

      case r=>
        false
    }
  }
  this.parsers = HasMemberParser::this.parsers

}

class TestStringModel(url:URI) extends SemanticModel(url){
  self=>
  var strings =Map.empty[URI,String]



  object StrParser extends self.ModelParser{
    override def apply(model:self.type,con:BigdataSailRepositoryConnection, st:Statement,path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Boolean = st.getObject match {
      case lit:org.openrdf.model.Literal=>
        val m= model
        model.strings = model.strings + (st.getPredicate->lit.stringValue())
        true
      case r=>
        false
    }
  }
  self.parsers = StrParser::self.parsers

}