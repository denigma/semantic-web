package org.denigma.semantic.sparql


object CONSTRUCT {

  def apply(triplets:TripletPattern*) = new ConstructQuery()

}



class ConstructQuery(triplets:TripletPattern*) extends  WithWhere with GP{
  //TODO implement
  override def stringValue: String = s"CONSTRUCT"

  override def children: List[GroupElement] = triplets.toList
}

