package org.denigma.semantic.controllers

import scala.concurrent.duration._
import akka.pattern.ask
import org.denigma.semantic.actors.readers.protocols.{StringShapeRead, ShapeRead}
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.{ArcRule, PropertyModel, Shape}
import org.scalax.semweb.sparql.SelectQuery

import scala.util.Try

trait ShapeController extends SimpleQueryController
{

  def selectWithShapeRes(query:SelectQuery,shape:Res) =  this.reader.ask(ShapeRead.SelectWithShapeRes(query,shape))(readTimeout).mapTo[Try[(Shape,Set[PropertyModel])]]

  def selectWithShape(query:SelectQuery,shape:Shape) =  this.reader.ask(ShapeRead.SelectWithShape(query,shape))(readTimeout).mapTo[Try[Set[PropertyModel]]]

  def selectWithShapes(query:SelectQuery,shapes:Map[String,Shape]) =  this.reader.ask(ShapeRead.SelectWithShapes(query,shapes))(readTimeout).mapTo[Try[Set[PropertyModel]]]

  def stringSelectWithShapeRes(select:String,shape:Res) = reader.ask(StringShapeRead.SelectWithShapeRes(select,shape)).mapTo[Try[(Shape,Set[PropertyModel])]]

  def stringSelectWithShape(select:String,shape:Shape) = reader.ask(StringShapeRead.SelectWithShape(select,shape)).mapTo[Try[Set[PropertyModel]]]

  def loadArc(res:Res) = this.reader.ask(ShapeRead.LoadArc(res))(readTimeout).mapTo[Try[ArcRule]]

  def loadShape(res:Res) =  this.reader.ask(ShapeRead.LoadShape(res))(readTimeout).mapTo[Try[Shape]]

  def loadShapes(resources:Set[Res]) =  this.reader.ask(ShapeRead.LoadShapes(resources))(readTimeout).mapTo[Try[List[Shape]]]

  def loadAllShapes() = this.reader.ask(ShapeRead.LoadAllShapes())(readTimeout).mapTo[Try[List[Shape]]]

  def loadShapesForType(iri:IRI) = reader.ask(ShapeRead.LoadShapesForType(iri)).mapTo[Try[List[Shape]]]

  def loadPropertyModels(resources:Set[Res],shape:Shape,contexts:List[Res] = List.empty) =
    this.reader.ask(ShapeRead.LoadPropertyModels(shape,resources,contexts))(readTimeout).mapTo[Try[List[PropertyModel]]]


  //def select(query:SelectQuery):Future[Try[TupleQueryResult]] = reader.ask(query)(readTimeout).mapTo[Try[TupleQueryResult]]


}

