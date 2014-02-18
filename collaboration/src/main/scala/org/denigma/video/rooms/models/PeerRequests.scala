package org.denigma.video.rooms.models

import org.denigma.actors.messages.EventLike
import play.api.libs.json._
import scala.{None, Some}
import play.api.libs.json.JsSuccess



object PeerRequests
{

  /**
   * Let you connect to others
   */
  trait PeerRequest extends EventLike{
    val from:List[String]
    val to:List[String]
  }

  case class OpenPeers(from:List[String],to:List[String]) extends PeerRequest{val name = PeerRequests.OPEN_PEERS}
  case class PeersOpened(from:List[String],to:List[String]) extends PeerRequest{val name = PeerRequests.PEERS_OPENED}

  case class CallPeers(from:List[String],to:List[String]) extends PeerRequest{val name = PeerRequests.CALL_PEERS}
  case class PeersCalled(from:List[String],to:List[String]) extends PeerRequest{val name =  PeerRequests.PEERS_CALLED}

  case class ClosePeers(from:List[String],to:List[String]) extends PeerRequest{val name = PeerRequests.CLOSE_PEERS}
  case class PeersClosed(from:List[String],to:List[String]) extends PeerRequest{val name =  PeerRequests.PEERS_CLOSED}


  case class FreePeers(name:String,from:List[String],to:List[String]) extends PeerRequest

  val OPEN_PEERS = "openpeers"
  val PEERS_OPENED = "peersopened"

  val CALL_PEERS = "callpeers"
  val PEERS_CALLED = "peerscalled"

  val CLOSE_PEERS = "closepeers"
  val PEERS_CLOSED = "peersclosed"


  def parse(name:String,from:List[String],to:List[String]):PeerRequest  = name match
  {
    case this.OPEN_PEERS => OpenPeers(from,to)
    case this.PEERS_OPENED =>PeersOpened(from,to)

    case this.CALL_PEERS =>CallPeers(from,to)
    case this.PEERS_CALLED =>PeersCalled(from,to)

    case this.CLOSE_PEERS => ClosePeers(from,to)
    case this.PEERS_CLOSED => PeersClosed(from,to)
    case str:String => FreePeers(str,from,to)
  }

  /**
   * reads JSON an creates PeerRequest instance
   */
  implicit val readPeers:Reads[PeerRequest] = new Reads[PeerRequest] {
    //TODO: make safe
    def reads(json: JsValue): JsResult[PeerRequest] = {
      val from:List[String] = (json \ "from").asOpt[List[String]] match
      {
        case Some(list:List[String]) => list
        case None=>Nil
      }
      val to:List[String] = (json \ "to").asOpt[List[String]] match
      {
        case Some(list:List[String])=>list
        case None =>Nil
      }

      val res:PeerRequest = (json \"name").asOpt[String] match
      {

        case Some(str:String) => PeerRequests.parse(str,from,to)
        case None=>OpenPeers(from,to)

      }
      JsSuccess(res)


    }
  }

  implicit val writePeers:Writes[PeerRequest] = new Writes[PeerRequest] {
    def writes(o: PeerRequest): JsValue = {
      val jName = Json.toJson[String](o.name)
      val jFrom = Json.toJson(o.from)
      val jTo = Json.toJson(o.to)
      Json.obj("name"->jName,"from"->jFrom,"to"->jTo)
    }
  }
  implicit val PeersFormat: Format[PeerRequest] = Format(this.readPeers,this.writePeers)

}



