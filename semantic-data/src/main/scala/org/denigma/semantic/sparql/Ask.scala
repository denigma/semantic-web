package org.denigma.semantic.sparql

import org.denigma.semantic.model.{IRI, QueryElement}


object ASK
{

  def apply(params:QueryElement*): AskQuery = {
    val q = new AskQuery()
    if(params.length>0) q.WHERE(params:_*)
    q
  }
}

class AskQuery extends WithWhere
{
  def stringValue = s"ASK  \n${WHERE.stringValue}\n"



}

//TODO: make full support of from
case class From(iri:IRI) extends QueryElement {


  override def stringValue: String = s"\nFROM <${iri.stringValue}> \n"
}
