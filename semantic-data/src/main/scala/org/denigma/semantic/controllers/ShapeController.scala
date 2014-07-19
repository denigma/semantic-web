package org.denigma.semantic.controllers

import akka.pattern.ask
import org.denigma.semantic.actors.readers.protocols.{ShapeStringRead, ShapeRead}
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.{PropertyModel, Shape}
import org.scalax.semweb.sparql.SelectQuery

import scala.util.Try

trait ShapeController extends SimpleQueryController{

  def selectWithShapeRes(query:SelectQuery,shape:Res) =  this.reader.ask(ShapeRead.SelectWithShapeRes(query,shape))(readTimeout).mapTo[Try[List[PropertyModel]]]

  def selectWithShape(query:SelectQuery,shape:Shape) =  this.reader.ask(ShapeRead.SelectWithShape(query,shape))(readTimeout).mapTo[Try[List[PropertyModel]]]

  def selectWithShapes(query:SelectQuery,shapes:Map[String,Shape]) =  this.reader.ask(ShapeRead.SelectWithShapes(query,shapes))(readTimeout).mapTo[Try[List[PropertyModel]]]


  def stringSelectWIthShapeRes(select:String,shape:Res) = reader.ask(ShapeStringRead.SelectWithShapeRes(select,shape)).mapTo[Try[List[PropertyModel]]]

  def stringSelectWIthShape(select:String,shape:Shape) = reader.ask(ShapeStringRead.SelectWithShape(select,shape)).mapTo[Try[List[PropertyModel]]]


  def loadShape(iri:IRI) =  this.reader.ask(ShapeRead.LoadShape(iri))(readTimeout).mapTo[Try[List[PropertyModel]]]

  def loadShapesForType(iri:IRI) = reader.ask(ShapeRead.LoadShapesForType(iri)).mapTo[Try[List[Shape]]]

  def loadProperties(iri:IRI,shape:Shape,contexts:List[Res] = List.empty) =
    this.reader.ask(ShapeRead.LoadProperties(shape,iri,contexts))(readTimeout).mapTo[Try[List[PropertyModel]]]






  //def select(query:SelectQuery):Future[Try[TupleQueryResult]] = reader.ask(query)(readTimeout).mapTo[Try[TupleQueryResult]]


}

