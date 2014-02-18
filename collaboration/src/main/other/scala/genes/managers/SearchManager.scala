package org.denigma.genes.managers


import org.denigma.actors.staff._
import org.denigma.genes.models._
import play.api.libs.json.{JsError, JsSuccess, Json, JsValue}
import org.denigma.actors.models.{ RequestInfo}
import java.util.Date
import org.denigma.actors.messages.{Push, ActorEvent}

/**
 * Manager that executes all lookup and search functionality except for traversals
 */
trait SearchManager extends SuggestionManager {


  def receiveSearch:  this.Receive= {
    /**
     * When you are asked to send the message to the client
     */
    /**
     * When you are asked to send chat message to the client
     */
    case Push(date, value:Suggestion) =>
      val req = pack2Request("genes",value,"lookup")
      this.sendJson2Client(req)(date)
  }

  def parseSearch:this.ChannelRequestRoomContentDateParser = {

    case ("genes","lookup", room, content, date)=>
      parseSuggest("lookup",content)(date)


    case ("genes","search",room,content,date) =>  log.error("search parsing is not yet implemented")
  }

}


/**
 * Process suggestions
 */
trait SuggestionManager extends ChatActorLike with SuggestionFormatter with SuggestFormatter
{


  def pack2Request(channel:String,sug:Suggestion,request:String):JsValue =
    Json.toJson(pack2RequestInfo(channel:String,sug,request))(writeRequestInfo)

  def pack2RequestInfo(channel:String,sug:Suggestion,request:String): RequestInfo = {
    val content: JsValue =  Json.toJson(sug)(writeSuggestion)
    RequestInfo(channel,content,request, "all")
  }


  /**
   * Does operations with tasks
   * @param req request of lookup, "lookup" by default
   * @param content json content to be parsed
   * @param date date when we received it
   * @return
   */
  def parseSuggest(req:String,content:JsValue)(implicit date:Date)=  {

    content.validate[Suggest](this.readSuggest) match
    {
      case result:JsSuccess[Suggest] =>
        val ev = ActorEvent(result.value.field,result.value,self)
        this.publish(ev)
        log.debug(s"${name}: Suggest with content ${content.toString()}  received at ${date.toString} and ActorEvent for it created")


      case result:JsError=>
        log.error(s"${name}: error in parsing Suggest json: unable to parseOperations ${content.toString()} at ${date.toString} ")
    }

  }




}


