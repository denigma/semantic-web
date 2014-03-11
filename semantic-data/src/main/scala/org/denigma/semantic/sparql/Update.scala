package org.denigma.semantic.sparql
import org.denigma.semantic.sparql._

import org.denigma.semantic.model.{Trip, QueryElement, IRI}



case class InsertDeleteQuery(insert:Insert,delete:Delete)
case class DeleteInsertQuery(delete:Delete,insert:Insert)

case class InsertQuery(insert:Insert)

case class DeleteQuery(delete:Delete)

class Insert(var children: List[QueryElement]) extends GP with WithWhere
{

  lazy val hasDATA = children.exists(_.isInstanceOf[Data])

  override def stringValue: String = if(hasDATA) s"INSERT ${this.foldChildren} "
    else s"INSERT \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object INSERT
{
  def apply(data:Data): Insert = new Insert(data::Nil)

  def apply(graph:PatternGraph) = new Insert(graph::Nil)
}



/**
 * Class to Delete values
 */
class Delete(var children: List[QueryElement]) extends GP with WithWhere {

  lazy val hasDATA = children.exists(_.isInstanceOf[Data])

  override def stringValue: String = if(hasDATA) s"DELETE${this.foldChildren} "
  else s"DELETE \n{ ${this.foldChildren} } "+ WHERE.stringValue

}

object DELETE {
  def apply(data:Data): Delete = {
    new Delete(data::Nil)
  }

  def apply(graph:PatternGraph): Delete = {
    new Delete(graph::Nil)
  }
}


object DATA {

  def apply(triplets:Trip*) = {
    new Data(triplets.toList)

  }

  def apply(graph:TripletGraph) = new Data(graph::Nil)

}

object GRAPH {

  def apply(id:IRIPatEl,triplets:Trip*) = new TripletGraph(id)(triplets.toList)
  def apply(id:IRIPatEl,patterns:TripletPattern*) = new PatternGraph(id)(patterns.toList)
}



class Data(val children:List[QueryElement]) extends GP {


  override def stringValue: String = s"DATA \n{ ${this.foldChildren} }\n"


}

