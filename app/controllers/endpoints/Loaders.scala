package controllers.endpoints

import org.denigma.semantic.controllers.ShapeController
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.Shape
import spray.caching.{Cache, LruCache}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait ShapeLoader extends ShapeController{


  val shapes:Cache[Try[List[Shape]]] = LruCache(timeToLive = 10 minutes)

  val shape:Cache[Try[Shape]] = LruCache(timeToLive = 10 minutes)

  def getShape(shapeId:Res) = shape(shapeId.stringValue){
    this.loadShape(shapeId)
  }

  def getShapes(resources:Set[Res]) = shapes(resources.hashCode.toString){
    this.loadShapes(resources)
  }


}
