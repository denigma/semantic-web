package org.denigma.semantic.users

import org.denigma.semantic.model.{Quad, IRI, Res}
import org.denigma.semantic.sparql.SelectQuery

case class ContextPermission(account:IRI) {

  def allowRead(context:IRI) = context.stringValue match {
    case cont if cont.contains("private")  || cont.contains("secure")=> cont.contains(account.stringValue)
    case _=>true
  }

  def allowWrite(context:IRI) = context.stringValue match {
    case cont if cont.contains(account.stringValue)=>true
    case _ =>false

  }

}



trait Permission


//trait Permission {
//
//  def allowSelect(pat:SelectQuery):Boolean
//  def allowAsk(pat:SelectQuery):Boolean
//  def allowWrite(pat:Quad):Boolean
//
//}
