package org.denigma.semantic.sparql

import org.denigma.semantic.model.QueryElement


object CONSTRUCT {

  def apply(triplets:TripletPattern*) = new ConstructQuery()

}



class ConstructQuery(triplets:TripletPattern*) extends  WithWhere with GP{
  //TODO implement
  override def stringValue: String = s"CONSTRUCT"

  override def children: List[QueryElement] = triplets.toList
}

