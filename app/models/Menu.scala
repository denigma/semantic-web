package models

import org.denigma.semantic.Prefixes
import org.openrdf.model.impl.URIImpl
import org.openrdf.model.{vocabulary, Statement, URI, Resource}
import org.denigma.semantic.classes.SemanticModel

//object Menu{
//  val child = new URIImpl(Prefixes ui "child")
//}
///*
//Menu class for models to load
//TODO: complete the class
// */
//class Menu(url:Resource) extends SemanticPage(url){
//
//
//
//  var children: Seq[SemanticPage] = List.empty[SemanticPage]
//
//  override def propertiesHandler(oldVal:Map[URI, Seq[Statement]],newVal:Map[URI, Seq[Statement]]):Unit  = {
//    super.propertiesHandler(oldVal,newVal)
//    children = List.empty[SemanticPage]
//    newVal.get(Menu.child).foreach{v=>
//      //      this.children = v.view.map{
//      //        st=>st.getObject match {
//      //        case st:Statement if st.getPredicate => new SemanticPage(st.getObject)
//      //        case _=>play.api.Logger.error(s"unknown predicate")
//      //      }
//    }
//
//
//  }
//}
//
//object SemanticPage{
//
//}
//
///*
//Menu class for models to load
//TODO: complete the class
// */
//class SemanticPage(url:Resource) extends SemanticModel(url){
//
//  override def addStatements(statements:Seq[Statement]) = {
//    super.addStatements(statements)
//    this.properties
//
//
//  }
//
//  var label:String = ""
//
//  override def propertiesHandler(oldVal:Map[URI, Seq[Statement]],newVal:Map[URI, Seq[Statement]]):Unit  = {
//    newVal.get(vocabulary.RDFS.LABEL).foreach{
//      st=>
//        if(!st.isEmpty)this.label=st.head.getObject.stringValue()
//    }
//  }
//
//
//}