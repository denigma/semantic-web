package controllers

import play.api.libs.json.{JsObject, Json, JsValue}
import scala.IndexedSeq
import play.api.mvc.{Controller, Action}

/**
 * Created by antonkulaga on 11/30/13.
 */
trait GenGraph extends Controller {

  def genNodes(ns:Int=5, prs:Int=5): JsValue = {
    def props = Json.toJson((0 until prs).map(r => ("property_" + r, "value+" + r)).toMap)

    val nodes: IndexedSeq[JsObject] = (0 until ns).map(v =>
      Json.obj("id" -> v.toString,
        "name" -> ("node_name_" + v.toString),
        "description" -> ("DESC about " + v.toString),
        "properties" -> props
      )
    )
    Json.toJson(nodes)

  }


  def genEdges: JsValue = {
    def props = Json.toJson((1 until 2).map(r => ("property_" + r, "value+" + r)).toMap)
    val edges: IndexedSeq[JsObject] = (1 until 5).map(e =>
      Json.obj("id" -> e.toString,
        "name" -> ("edge_name_" + e.toString),
        "description" -> ("DESC about " + e.toString),
        "properties" -> props
        , "incoming" -> genNodes(2,2)
        , "outgoing" -> genNodes(3, 3)

      )
    )

    Json.toJson(edges)
  }




  def hallo = Action {
    implicit request =>
      Ok(views.html.hallo()) //Ok(views.html.page("node","menu","0"))
  }

}
