package org.denigma.semantic

import org.openrdf.model.impl.URIImpl
import org.openrdf.model.URI

/*
Helper that contains some widely used prefixes
 */
object Prefixes {
  import com.bigdata.rdf.vocab.decls
  import org.openrdf.model.vocabulary
//  val FN = vocabulary.FN
//  val OWL = vocabulary.OWL
//  val RDF = vocabulary.RDF
//  val RDFS = vocabulary.RDFS
//  val SESAME = vocabulary.SESAME
//  val XML_SCHEMA = vocabulary.XMLSchema
//  object DUBLIN_CORE {
//    type ELEMENTS = decls.DCElementsVocabularyDecl
//    val TERMS =  decls.DCTermsVocabularyDecl
//  }
//
//  val FOAF = decls.FOAFVocabularyDecl
//  val SKOS = decls.SKOSVocabularyDecl
//  val VOID = decls.VoidVocabularyDecl

  /*
Webintelligence vocabulary
 */


  object Denigma extends Denigma

  object ILA extends ILA


}
object WI extends WI
/*
refactor
 */
class WI
{
  import com.bigdata.rdf.model._
  import org.openrdf.model._
  def wi(str:String) = s"http://webintelligence.eu/$str/"

  def re(str:String) = new URIImpl(RESOURCE+s"/$str/")
  def page(str:String) = new URIImpl(PAGES+s"/$str/")
  def po(str:String) = new URIImpl(POLICY+s"/$str/")


  val RESOURCE: String = this wi "resource"
  val CONFIG:String = this wi "config"
  val PAGES: String = this wi "pages"
  val USERS:String = this wi "users"
  val ORGANIZATIONS = this wi "orgs"
  val POLICY:String= this wi "policy"
  val SETTINGS:String=  wi("settings")

  def set(str:String): URI= this set new URIImpl(SETTINGS+s"/$str/")
  def set(res:URI): URI = { uris=uris+res; res}


  val root: URI= this set "root"
  val context = this set "context"

  var uris: Set[URI] = Set.empty[URI]

}

class ILA
{

  val NAMESPACE = this ila ""
  def ila(str:String) = s"http://longevityalliance.org/$str/"
  val RESOURCE: String = this ila "resource"
  val PAGES = this ila "pages"

}

class Denigma
{
  val MAIN:String = "http://denigma.org/resource/"
  val OBO:String = "http://purl.obolibrary.org/obo/"

}