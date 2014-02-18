package org.denigma.actors

import messages._

import play.api.libs.json._
import java.util.Date

import scala.collection.immutable._
import org.denigma.actors.managers._
import org.denigma.actors.staff.EventActor
import scala._
import play.api.libs.json.JsSuccess
import org.denigma.actors.messages.Push
import org.denigma.actors.models.RequestInfo
import org.denigma.actors.messages.Received
import org.denigma.actors.messages.Start
import play.api.libs.iteratee.Concurrent.Channel
import org.denigma.actors.rooms.messages.TellOthers
import akka.actor._
import scala.concurrent.duration._
import org.denigma.actors.models.RequestInfo
import play.api.libs.json.JsSuccess
import org.denigma.actors.messages.Received
import org.denigma.actors.messages.Push
import org.denigma.actors.messages.Start
import org.denigma.actors.rooms.messages.TellOthers
import scala.Some

/**
 * this is a user actor who is currently connected to the chat
 * usually each websocket connection creates a Member actor
 * */
class Member extends EventActor with MessageManager with TaskManager with UserManager
{

  def receiveOther:  this.Receive= {

    /**
     * When there is a message you do not know what to do about
     */
    case some=>this.logUnregisteredInput(some)
  }


  def onPusher(pusher:Channel[JsValue],connection:String) =
  {

    this.channels = this.channels + (connection->pusher)
    if(this.toSend.isEmpty==false){
      this.toSend = scala.collection.immutable.List.empty[JsValue]
    }
  }


  def receiveBasic: this.Receive= {

    /**
     * When channel for communication with client received
     */
    case Start(pusher: Channel[JsValue],connection) => this.onPusher(pusher,connection)
    /**
     * When any message from the client received
     */
    case received:Received[_] => this.processReceived(received)

    /**
     * When you are asked to send some rawjson to the client
     */
    case Push(date, value:JsValue) => sendJson2Client(value)(date)

    /**
     * When you are asked to send some rawjson to the client
     */
    case Push(date, value:RequestInfo) => sendRequest2Client(value)(date)

    /**
     * When you are asked to the the content of the sorted set to the client
     */
    case Push(date, value:SortedSet[DataLike]) => value.foreach(data=>this.receive(data))


  }



  /**
   * input of user actor
   *
   * warning: Scala does not work well with generic case classes sometimes
   * */
  def receive =  this.receiveBasic orElse
    this.receiveRoom orElse
    this.receiveMessage orElse
    this.receiveTask orElse
    this.receiveStatus orElse this.receiveOther

  /**
   * Works with all content wrapped in Received
   * @param rec message received from the client
   */
  def processReceived(rec:Received[_]): Unit = rec match
  {
    /**
     * If you received a json value we should parseOperations it
     */
    case Received(date,value:JsValue,connection)=>this.parse(receiveJson(value)(date))

    case Received(date,value:String,connection)=>this.parse(receiveJson(Json.parse(value))(date))

    /**
     * you do not know what you received
     */
    case some =>  this.logUnregisteredInput(some)

  }



  /**
   * Parses received JSON
   * @param value JSON value to be send
   * @param date date when it is sent
   * @return
   */
  def receiveJson(value:JsValue)(date:Date):JsValue =
  {
    this.logReceivedJson(value)(date)
    value
  }


  def parseOperations: this.ChannelRequestRoomContentDateParser =
    this.parseMessage orElse parseTask orElse parseUsers orElse this.parseOther



  /**
   * parses incoming JSON
   * */
  def parse(data:JsValue)(implicit date:Date): Unit =
  {
    data.validate[RequestInfo](requestInfoFormat) match
    {
      case result:JsSuccess[RequestInfo] =>
        //TODO: rewrite with requestinfo and delete dates!
        this.parseOperations( (result.value.channel, result.value.request, result.value.room, result.value.content, date) )
      case result:JsError=>
        this.logWrongRequestInfoParsing(date)

    }
  }

  /**
   * Writes to cache and informs others
   * @tparam T type of the data to be told
   */
  def inform[T](room:String,value:T)
  {
    this.rooms.get(room) match
    {
      case Some(roomActor)=> roomActor ! TellOthers(name,value)
      case None => this.logCannotFindRoom(room)
    }
    this.logOtherInfo()
  }


}
