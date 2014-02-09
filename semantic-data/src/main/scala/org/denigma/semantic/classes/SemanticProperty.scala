package org.denigma.semantic.classes

import org.openrdf.model.URI
/*
this class represent property class of semanticweb
 */
class SemanticProperty(url:URI) extends SemanticClass(url){
  override val isProperty = true



  override def init(){
    object SemanticClassParser extends PropertyParser[this.type]
    this.parsers = SemanticClassParser::this.parsers
  }

}

class PropertyParser[SELF<:SemanticProperty] extends ClassParser[SELF]
{
//  override def apply(model:SELF,con:BigdataSailRepositoryConnection, st:Statement,path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Boolean = (st.getPredicate,st.getObject) match  {
//
//    case (p, o:Resource) if model.isDomain.(o)(con)=>
//      val f: SemanticClass = path.collectFirst{  case (k,sl:SemanticClass) if sl.url==o =>sl}
//        .getOrElse{
//        val sc = new SemanticClass(o)
//        sc.load(con,path,maxDepth)
//        sc
//      }
//      model.types = model.types + (o->f)
//      true
//
//    case _=>super.apply(model,con,st,path,maxDepth)
//
//  }

}