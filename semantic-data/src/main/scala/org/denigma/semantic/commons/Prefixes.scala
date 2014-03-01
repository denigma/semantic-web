package org.denigma.semantic.commons


/*
Helper that contains some widely used prefixes

TODO: FIX,DOES NOT WORK NOW
 */
//object Prefixes extends WI with UI{
//
//  self=>
//
//  object Pages {
//    val PAGE = self.pg("Page")
//    val MENU = self.pg("Menu")
//    val QUERY_PAGE = self.pg("Query_Page")
//    def pg(str:String) = new URIImpl(PAGES+s"/$str")
//  }
//
//
//  def uri(str:String) = new URIImpl(str)
//
////  def RDF = vocabulary.RDF
////  def RDFS = vocabulary.RDFS
//  import com.bigdata.rdf.vocab.decls
//  import org.openrdf.model.vocabulary
////  val FN = vocabulary.FN
////  val OWL = vocabulary.OWL
////  val RDF = vocabulary.RDF
////  val RDFS = vocabulary.RDFS
////  val SESAME = vocabulary.SESAME
////  val XML_SCHEMA = vocabulary.XMLSchema
////  object DUBLIN_CORE {
////    type ELEMENTS = decls.DCElementsVocabularyDecl
////    val TERMS =  decls.DCTermsVocabularyDecl
////  }
////
////  val FOAF = decls.FOAFVocabularyDecl
////  val SKOS = decls.SKOSVocabularyDecl
////  val VOID = decls.VoidVocabularyDecl
//
//  /*
//Webintelligence vocabulary
// */
//
//
//  object DENIGMA extends Denigma
//
//  object ILA extends ILA
//
//
//}
//
import org.openrdf.model._
import org.openrdf.model.impl._

object UI {
  def sp(str:String) = s"http://spinrdf.org/sp#$str"
  def spr(str:String) = s"http://spinrdf.org/spr#$str"
  def ui(str:String) = s"http://uispin.org/ui#$str"
}
/*
refactor
 */
object WI {

  def set(str:String): URI= this set new URIImpl(SETTINGS+s"/$str/")

  lazy val RESOURCE: String = this.wi("resource")

  lazy val CONFIG:String = this.wi("conf")

  lazy val PAGES: String = this.wi("pages")

  lazy val USERS:String = this.wi("users")

  lazy val ORGANIZATIONS = this.wi("orgs")

  lazy val POLICY:String= this.wi("policy")

  lazy val SETTINGS:String=  this.wi("settings")

  def re(str:String) = new URIImpl(RESOURCE+s"/$str")

  def pg(str:String) = new URIImpl(PAGES+s"/$str")

  def conf(str:String) = new URIImpl(CONFIG+s"$str")

  def po(str:String) = new URIImpl(POLICY+s"/$str")

  lazy val root: URI= this set "root"

  lazy val context = this set "context"

  var uris: Set[URI] = Set.empty[URI]


  def set(res:URI): URI = { uris=uris+res; res}

  def wi(str:String): String = ("http://webintelligence.eu/"+str)

}



class ILA
{

  val NAMESPACE = this ila ""
  def ila(str:String) = s"http://longevityalliance.org/$str"
  val RESOURCE: String = this ila "/resource"
  val PAGES = this ila "pages"

}

class Denigma
{
  val DE:String = "http://denigma.org/resource/"
  val OBO:String = "http://purl.obolibrary.org/obo/"
  def de(str:String) = s"http://denigma.org/resource/$str"

}