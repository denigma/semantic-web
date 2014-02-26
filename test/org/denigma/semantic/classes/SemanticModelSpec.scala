package org.denigma.semantic.classes

import org.openrdf.model._
import com.bigdata.rdf.vocab.decls
import org.junit.runner.RunWith
import org.openrdf.model.impl.URIImpl
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication
import scala.util.Try
import org.denigma.semantic.platform.SP


@RunWith(classOf[JUnitRunner])
class SemanticModelSpec  extends Specification {
  val self = this

  val ant=new URIImpl("http://webintelligence.eu/ontology/actor/antonkulaga")
  val hev =new URIImpl("http://webintelligence.eu/ontology/actor/hevok")
  val il=new URIImpl("http://webintelligence.eu/ontology/actor/ILA")
  val de =new URIImpl("http://webintelligence.eu/ontology/actor/Denigma")

  "SemanticModel" should {

    "just parse statements with assignable handlers" in new WithApplication(){

      SP.platformParams.isEmpty should beTrue
      SP.db.parseFileByName("data/test/test_user.ttl")

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
      anton.load(SP.db)
      anton.strings.size shouldEqual(4)

      val hevok: TestStringModel = new TestStringModel(self.hev)
      hevok.strings.size shouldEqual(0)
      hevok.load(SP.db)
      hevok.strings.size shouldEqual(3)

      val ila: TestStringModel = new TestStringModel(self.il)
      ila.load(SP.db)
      ila.strings.size shouldEqual(1)

      val denigma: TestStringModel = new TestStringModel(self.de)
      denigma.load(SP.db)
      denigma.strings.size shouldEqual(1)


    }

    "parse complex structures(organizations)" in new WithApplication(){

      SP.platformParams.isEmpty should beTrue
      SP.db.parseFileByName("data/test/test_user.ttl")


      val denigma: TestOrganizationModel = new TestOrganizationModel(self.de)
      denigma.load(SP.db)
      denigma.strings.size shouldEqual(1)
      denigma.hasMember.size shouldEqual(2)
      denigma.hasMember.contains(self.hev) should beTrue
      denigma.hasMember.contains(self.ant) should beTrue

      denigma.hasMember.get(ant).get.strings.size shouldEqual(4)
      denigma.hasMember.get(hev).get.strings.size shouldEqual(3)

    }

    "parse complex structures(users)" in new WithApplication(){

      SP.platformParams.isEmpty should beTrue
      SP.db.parseFileByName("data/test/test_user.ttl")

      val anton= new TestUserModel(self.ant)
      anton.strings.size shouldEqual(0)
      anton.load(SP.db)
      anton.strings.size shouldEqual(4)
      anton.memberOf.size.shouldEqual(2)

      val hevok = new TestUserModel(self.hev)
      hevok.strings.size shouldEqual(0)
      hevok.load(SP.db)
      hevok.strings.size shouldEqual(3)
      hevok.memberOf.size.shouldEqual(2)

      hevok.memberOf.contains(de) should beTrue

      val denigma: TestOrganizationModel = hevok.memberOf.get(de).get
      denigma.load(SP.db)
      denigma.strings.size shouldEqual(1)
      denigma.hasMember.size shouldEqual(2)
      denigma.hasMember.contains(self.hev) should beTrue
      denigma.hasMember.contains(self.ant) should beTrue

      denigma.hasMember.get(ant).get.strings.size shouldEqual(4)
      denigma.hasMember.get(hev).get.strings.size shouldEqual(3)

    }
  }

}


class TestUserModel(url:URI) extends TestStringModel(url)
{
  self=>


  var memberOf:Map[Resource,TestOrganizationModel] = Map.empty

  object MemberOfParser extends MemberOfParser[self.type]

  this.parsers = MemberOfParser::self.parsers

}

class MemberOfParser[SELF<:TestUserModel] extends TestModelParser[SELF]{

  def parsePropertyObject:onPropertyObject  = {

    case (out, p, o:org.openrdf.model.URI)  if out.path.contains(o) || out.model.memberOf.contains(o)=>


    case (out, p, o:org.openrdf.model.URI)  if out.con.hasStatement(o,vocabulary.RDF.TYPE,decls.FOAFVocabularyDecl.Organization,true)=>
      val organ =new TestOrganizationModel(o)

      out.model.memberOf = out.model.memberOf+(o->organ)
      organ.hasMember = organ.hasMember + (out.model.url->out.model)
      organ.loadAll(out)

    case _=>
  }


}


class TestOrganizationModel(url:URI) extends TestStringModel(url){
  self=>

  val user = new URIImpl("http://webintelligence.eu/ontology/actor/User")

  var hasMember:Map[Resource,TestUserModel]  = Map.empty

  object HasMemberParser extends HasMemberParser[self.type ]

  this.parsers = HasMemberParser::this.parsers

}

class HasMemberParser[SELF<:TestOrganizationModel] extends TestModelParser[SELF]{
  val user = new URIImpl("http://webintelligence.eu/ontology/actor/User")

  def parsePropertyObject:onPropertyObject  = {

    case (out, p, o:org.openrdf.model.URI) if out.path.contains(o) || out.model.hasMember.contains(o)=>


    case (out, p, o:org.openrdf.model.URI)  if out.con.hasStatement(o,vocabulary.RDF.TYPE,user,true)=>
      val um = new TestUserModel(o)
      out.model.hasMember = out.model.hasMember+(o->um)
      um.memberOf = um.memberOf + (out.model.url-> out.model)
      um.loadAll(out)


    case _=>
  }



}

class TestStringModel(val url:Resource) extends SemanticModel{
  self=>

  var strings =Map.empty[URI,String]


  object TestStringParser extends StrParser[self.type]

  self.parsers = TestStringParser ::self.parsers

  /*
   loads all properties of the resource
    */
  def loadAll(params:LoadParamsLike):Try[Unit] = {
    loadOutgoing(params)
  }

  def loadOutgoing(params:LoadParamsLike) = {
    this.loadWith(params.con.getStatements(url,null,null,true),params)((st,p)=>OutgoingParams[this.type](this,st,p,params.maxDepth)(params.con))
  }

}




class StrParser[SELF<:TestStringModel] extends TestModelParser[SELF]
{


  def parsePropertyObject:onPropertyObject  = {

    case (out, p,lit:org.openrdf.model.Literal)=>

        out.model.strings = out.model.strings + (out.st.getPredicate->lit.stringValue())

    case _=>

  }

}

abstract class TestModelParser[SELF<:TestStringModel] extends ModelParser[SELF]{

  def parsePropertyObject:onPropertyObject

  type onPropertyObject = PartialFunction[(OutgoingParams[SELF],URI,Value),Unit]

  override def parse:PartialFunction[TraverseParams[SELF],Unit] = {

    case out:OutgoingParams[SELF]=>parsePropertyObject((out,out.st.getPredicate,out.st.getObject))

    case _=>play.Logger.error(s"unknown parse params")
  }


}