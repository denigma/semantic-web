package org.denigma.semantic.sparql

trait WithWhere extends GroupElement
{
  self=>

  object WHERE extends WhereClause(List.empty)
  {
    where=>

    def apply(elements:GroupElement*):self.type = {
      this.children = elements.toList
      self
    }

  }


}

class WhereClause(var children:List[GroupElement]) extends GP
{
  where=>

  override def stringValue: String = if(!this.hasChildren) "" else s"WHERE \n{ ${this.foldChildren} }\n"
}

