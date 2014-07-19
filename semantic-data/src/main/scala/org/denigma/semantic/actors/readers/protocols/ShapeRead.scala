package org.denigma.semantic.actors.readers.protocols

import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.Shape
import org.scalax.semweb.sparql.SelectQuery


object ShapeStringRead {

  trait StringShExQuery

  case class SelectWithShapeRes(query:String, shapeRes:Res,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery

  case class SelectWithShapeResources(query:String, shapeRes:Map[String,Res],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery


  case class SelectWithShape(query:String, shape:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery
  case class SelectWithShapes(query:String, shapes:Map[String,Shape],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery

}
object ShapeRead {

  trait ShExQuery

  case class LoadShape(iri:IRI,contexts:List[Res] = List(IRI(WI.RESOURCE))) extends  ShExQuery

  case class LoadShapesForType(iri:IRI,contexts:List[Res] = List.empty) extends ShExQuery

  case class LoadProperties(shape:Shape,iri:IRI,contexts:List[Res] = List.empty)

  case class SelectWithShapeRes(query:SelectQuery, shapeRes:Res,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SelectWithShapeResources(query:SelectQuery, shapeRes:Map[String,Res],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SelectWithShape(query:SelectQuery, shape:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SuggestWithShape(typed:String,prop:IRI,shape:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery


  case class Suggest(typed:String,field:IRI,query:SelectQuery, shapes:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery


  case class SelectWithShapes(query:SelectQuery, shapes:Map[String,Shape] = Map.empty[String,Shape],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery


}
