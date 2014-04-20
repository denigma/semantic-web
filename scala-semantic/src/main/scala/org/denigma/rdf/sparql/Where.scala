package org.denigma.rdf.sparql

import org.denigma.rdf.model.QueryElement

trait WithWhere extends QueryElement
{
  self=>



  object WHERE extends WhereClause(List.empty)
  {
    where=>

    def apply(elements:QueryElement*):self.type = {
      this.children = elements.toList
      self
    }

  }


  def hasWhere = this.WHERE.hasChildren


}

class WhereClause(var children:List[QueryElement]) extends GP
{
  where=>

  override def stringValue: String = if(!this.hasChildren) "" else s"WHERE \n{ ${this.foldChildren} }\n"
}

