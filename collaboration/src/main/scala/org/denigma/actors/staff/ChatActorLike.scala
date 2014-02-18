package org.denigma.actors.staff
import scala.concurrent.duration._
import org.denigma.actors.models._
import play.api.libs.json.{Json, JsValue}
import java.util.Date
import play.api.libs.iteratee.Concurrent.Channel
import akka.util.Timeout
import scala.Predef._
import play.api.libs.json.JsSuccess
import scala.Some
import org.denigma.actors.models.RequestInfo
import org.denigma.actors.messages.Received
import akka.actor.ActorRef
import scala.collection.immutable._
import scala.collection.immutable.Map


/**
 * Abstract trait for chat-related functionality
 */
trait ChatActorLike extends EventActor with RequestInfoFormatter
{

  implicit val timeout = Timeout(5 seconds) // needed for `?` below

  var rooms =  Map.empty[String,ActorRef]

  /**
   * type that is useful for parsing
   */
  type ChannelRequestRoomContentDateParser = PartialFunction[(String,String,String, JsValue, Date),Unit]

  /**
   * Type of actor receive function, useful for orElse concatinations
   */
  //type ReceiveFunction = PartialFunction[Any,Unit]


  /**
   * Abstract function to inform about something that received
   * @param data
   * @tparam T
   */
  def inform[T](room:String,data:T)


  /**
   * Extract ide from the content
   * @param content
   * @return
   */
  def getId(content:JsValue) = (content \ "id").as[String]

  /**
   * Get a set withouth and element with specified id
   * @param set
   * @tparam T
   */
  def getWithout[T<:  IdOwner](set:SortedSet[Received[T]], id:String): SortedSet[Received[T]] =
  {
    set.find(r=>r.value.id==id) match
    {
      case None=>set

      case Some(mess)=> set - mess
    }
  }



  /**
   * function needed for adding items to the SorthedSet if parsing was successful
   * @param result succesful result of JSON request
   * @param date date of the even (implicit)
   * @param getter implicit getter of the SorthedSet
   * @param setter  implicit setter of the SorthedSet
   * @tparam T type parameter for the message to be extracted
   * @return Some[result]
   */
  def addItemOnSuccess[T<:  IdOwner](result:JsSuccess[T])
    (date:Date)(implicit getter:SortedSet[Received[T]], setter: SortedSet[Received[T]] => Unit ):Option[Received[T]] =
      this.addItem(result.value)(date)

  /**
   * Add item to the map inside the actor
   * @param model model to be saved
   * @param date date when it happened
   * @param getter getter that gets the item from the Map
   * @param setter setter that add the item
   * @tparam T type of the item
   * @return
   */
  protected def addItem[T<:  IdOwner](model:T)(date:Date)(implicit getter:SortedSet[Received[T]], setter: SortedSet[Received[T]] => Unit ):Option[Received[T]] =   {
    val data = Received(date,model)
    setter(this.getWithout(getter,data.value.id) + data)
    return Some(data)
  }



  var channels = Map.empty[String,Channel[JsValue]]


  /**
   * Variable that caches messages to be send to the client in case if channel is not open
   */
  var toSend: List[JsValue] = List.empty[JsValue]


  /**
   * Send
   * @param value
   */
  def send(value:JsValue) = if(this.channels.isEmpty) this.toSend=value::toSend else this.channels.values.foreach(v=>v.push(value))


  /**
   * Sends several json to the client
   * @param list list of messages to be sent
   * @param date date when message is sent
   * @return
   */
  def sendJson2Client(list:List[JsValue])(date:Date): List[JsValue] = list.map(m=>this.sendJson2Client(m)(date))


  /**
   * Send request info to the client
   * @param req requestInfo
   * @param date date (if there is not date specified it is taken from implicit variables, like this.now)
   * @return jsvalue
   */
  def sendRequest2Client(req:RequestInfo)(implicit date:Date): JsValue =
  {
    val js = Json.toJson(req)(writeRequestInfo)
    this.sendJson2Client(js)(date)
  }

  def sendRequest2Client(list:List[RequestInfo])(implicit date:Date): List[JsValue]  = list.map(m=>this.sendRequest2Client(m)(date))


  def sendJson2Client(value:JsValue)(date:Date):JsValue =
  {
    this.send(value)
    this.logSend(value)(date)
    value
  }

  def parseOther: this.ChannelRequestRoomContentDateParser =  {
    case (channel,req, room, content:JsValue, date)=>
      val str = content.toString()
      log.error(s"$name:for channel '$channel' with request '$req' requestInfo parsing is not implemented, content = $str")
  }






}
