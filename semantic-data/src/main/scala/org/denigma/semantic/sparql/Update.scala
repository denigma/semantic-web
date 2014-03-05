package org.denigma.semantic.sparql


/**
 * Class to Insert/Delete values
 */
class Delete extends WithWhere {
  override def stringValue: String = s"DELETE ${WHERE.stringValue}"
}




class Insert extends GP with WithWhere
{
  override def stringValue: String = s"INSERT ${WHERE.stringValue}"

  override def children: List[GroupElement] = ???

}

object DELETE {


}


object INSERT {

}

trait WithData {

  object DATA {

    def apply(triplets:Trip*) = {
      new Data(triplets.toList)

    }

    def GRAPH(iri:IRIPatEl)(triplets:Trip*) = new Data(new TripletGraph(iri)(triplets.toList)::Nil)

  }
}


class Data(val children:List[GroupElement]) extends GP {


  override def stringValue: String = s"DATA \n{ ${this.foldChildren} }\n"


}

