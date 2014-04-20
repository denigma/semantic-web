package org.denigma.rdf.model.vocabulary

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
//    def pg(str:String) = IRI(PAGES+s"/$str")
//  }
//
//
//  def uri(str:String) = IRI(str)
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


object UI {
  def sp(str:String) = s"http://spinrdf.org/sp#$str"
  def spr(str:String) = s"http://spinrdf.org/spr#$str"
  def ui(str:String) = s"http://uispin.org/ui#$str"
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