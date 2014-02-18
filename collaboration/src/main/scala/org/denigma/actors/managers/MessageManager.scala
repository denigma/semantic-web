package org.denigma.actors.managers

import play.api.libs.json._
import java.util.Date
import scala.collection.immutable.SortedSet
import org.denigma.actors.orderings.DataOrdering
import play.api.libs.json.JsSuccess
import org.denigma.actors.messages.{Push, Received}
import org.denigma.actors.models._
import scala.Some
import org.denigma.actors.staff.ChatActorLike

/**
 * Trait encapsulates all member's functionality related to messages
 */
trait MessageManager extends ChatActorLike with MessageFormatter
{

  /**
   * here we stored messages that we received
   */
  protected var _messages = SortedSet.empty[Received[Message]](new DataOrdering[Received[Message]])

  implicit def messages = _messages

  implicit def messages_= (value:SortedSet[Received[Message]]): Unit = _messages = value

    /**
   * All actions that are needed if you received a message
   * @param req request (save, remove or any other)
   * @param content content with the information being sent
   * @param date date (when send)
   * @return
   */
  def processMessage(room:String,req:String,content:JsValue)(implicit date:Date): Unit =  req match
  {
    case "save" => addMessage(content)(date) match
    {
      case Some(data: Received[Message])=>this.inform(room,data.value)

      case None=> log.info(s"$name : cannot add message because it is not valid")
    }

    case "remove" | "delete"=> this.messages = this.getWithout(messages,this.getId(content))

    case _ =>  this.logRequestIsNotImplemented("message",req)
  }

  /**
   * adds message
   * @param content JSON content to be parsed
   */
  def addMessage(content:JsValue)(implicit date:Date): Option[Received[Message]] =
  {
    content.validate[Message](readMessage) match
    {
      case result:JsSuccess[Message] => this.addItemOnSuccess[Message](result)(date)

      case result:JsError=>
        log.error(s"${name}: error in parsing Message json: unable to parseOperations ${content.toString()} at ${date.toString} ")
        return None
    }
  }


  /**
   * Pack it in json request to be send to the client
   * @param message chat message to be packed
   * @return
   */
  def pack2Request(message:Message, request:String):JsValue = Json.toJson(pack2RequestInfo(message,request))(writeRequestInfo)

  /**
   * Pack the message to requestinfo wrapper
   * @param message chat message to be wrapped
   * @return
   */
  def pack2RequestInfo(message:Message, request:String): RequestInfo = {
    val content: JsValue =  Json.toJson(message)(writeMessage)
    RequestInfo("messages",content,request)
  }


  def receiveMessage: this.Receive= {
    /**
     * When you are asked to send the message to the client
     */
    /**
     * When you are asked to send chat message to the client
     */
    case Push(date, value:Message) => this.sendJson2Client(pack2Request(value,"push"))(date)
  }

  /**
   * Parses message
   * @return
   */
  def parseMessage:this.ChannelRequestRoomContentDateParser= {
    case ("messages",req, room, content, date) => this.processMessage(room,req,content)(date)
  }

}
