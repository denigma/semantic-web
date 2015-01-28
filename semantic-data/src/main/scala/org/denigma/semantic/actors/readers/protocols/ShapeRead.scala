package org.denigma.semantic.actors.readers.protocols

import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.Shape
import org.scalax.semweb.sparql.SelectQuery


object StringShapeRead {

  trait StringShExQuery

  case class SelectWithShapeRes(query:String, shapeRes:Res,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery

  case class SelectWithShapeResources(query:String, shapeRes:Map[String,Res],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery

  case class SelectWithShape(query:String, shape:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery

  case class SelectWithShapes(query:String, shapes:Map[String,Shape],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with StringShExQuery

}
object ShapeRead {

  trait ShExQuery

  case class LoadArc(iri:Res,contexts:Seq[Res] = Seq.empty) extends ShExQuery

  case class LoadShape(iri:Res,contexts:Seq[Res] = Seq.empty) extends  ShExQuery

  case class LoadShapes(resources:Set[Res], contexts:Seq[Res] = Seq.empty) extends ShExQuery

  case class LoadAllShapes(contexts:Seq[Res] = Seq.empty) extends ShExQuery

  case class LoadShapesForType(iri:IRI,contexts:List[Res] = List.empty) extends ShExQuery

  case class LoadPropertyModels(shape:Shape,prop:Set[Res],contexts:List[Res] = List.empty)

  //case class SelectStringWithShapeRes(query:String, shapeRes:Res,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SelectWithShapeRes(query:SelectQuery, shapeRes:Res,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SelectWithShapeResources(query:SelectQuery, shapeRes:Map[String,Res],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  //case class SelectStringWithShapeResources(query:String, shapeRes:Map[String,Res],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SelectWithShape(query:SelectQuery, shape:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery

  case class SuggestWithShape(typed:String,prop:IRI,shape:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery


  case class Suggest(typed:String,field:IRI,query:SelectQuery, shapes:Shape,offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery


  case class SelectWithShapes(query:SelectQuery, shapes:Map[String,Shape] = Map.empty[String,Shape],offset:Long = 0, limit:Long = Long.MaxValue,rewrite:Boolean = false) extends Paginated with ShExQuery


}
