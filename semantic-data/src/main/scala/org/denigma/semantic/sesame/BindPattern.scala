package org.denigma.semantic.sesame

import org.openrdf.model._
import org.denigma.rdf.{RDFValue, IRI, Res, PatternElement}
import org.denigma.sparql.{TripletPattern, QuadPattern, Variable}

/**
 * Created by antonkulaga on 18.04.14.
 */
object BindPattern {

  def canBind(p:QuadPattern,st:Statement) = (!p.hasContext || canBindValue(p.cont,st.getContext)) && canBindTriplet(p,st)

  def canBindTriplet(p:TripletPattern,st:Statement): Boolean = canBindValue(p.sub,st.getSubject) &&  canBindValue(p.pred,st.getPredicate) &&  canBindValue(p.obj,st.getObject)


  def canBindValue(p:PatternElement,value:Value) = p match {
    case v:Variable => true
    case null=> true
    case _ if value==null=>false
    case v:Value=> v.stringValue()==value.stringValue()
  }


}