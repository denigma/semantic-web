package org.denigma.genes.models


import play.api.libs.json._
import play.api.libs.functional.syntax._
import scalax.collection.GraphTraversal._
import scalax.collection.GraphTraversal.Direction
import org.denigma.genes.messages.GeneTraverse
import org.denigma.actors.messages.EventLike


/**
 * This FishEye class is a class that contains traversal instructions and can be (de)serialized to JSON
 * @param graph name of the graph to which we are reffering to
 * @param name name of the node we want to traverse
 * @param direction direction of traversal
 * @param depth depth
 * @param mode mode in which we work with the graph (reload, addition, etc0
 */
case class FishEye(graph:String,name:String,direction:String = GeneTraverse.direction2str(AnyConnected),depth:Int = 1,mode:String = "load") extends EventLike

trait FishEyeFormatter {

  /**
   * JSON reader for FishEye
   */
  implicit val readFishEye: Reads[FishEye] = (
    (__ \ "graph").read[String] ~
      (__ \ "name").read[String] ~
      (__ \ "direction").read[String] ~
      (__ \ "depth").read[Int] ~
      (__ \ "mode").read[String]
    )(FishEye)
  /**
   * JSON writer for FishEye
   */
  implicit val writeFishEye:Writes[FishEye] = (
    (__ \ "graph").write[String] ~
      (__ \ "name").write[String] ~
      (__ \ "direction").write[String] ~
      (__ \ "depth").write[Int] ~
      (__ \ "mode").write[String]
    )(unlift(FishEye.unapply))

  /**
   * JSON format for FishEye
   */
  implicit val fishEyeFormat: Format[FishEye] = Format(readFishEye, writeFishEye)

}
