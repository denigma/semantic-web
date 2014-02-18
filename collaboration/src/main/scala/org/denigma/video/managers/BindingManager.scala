package org.denigma.video.managers

import play.api.libs.json.JsValue
import java.util.Date
import org.denigma.actors.staff.ChatActorLike
import akka.actor.ActorRef
import org.denigma.actors.models.RequestInfo
import scala.Predef._
import org.denigma.actors.models.RequestInfo
import org.denigma.video.messages.{CloseBinding, ChangeBinding, OpenBinding}


/**
 * Companion object for BindingManagers, stores static functions
 */
object BindingManager
{
  //TODO: rename in the Future
  type ChannelRequestRoomContentDateChecker = (String,String,String, JsValue)=>Boolean

}

/**
 * Trait that does binding to other actors and channels
 */
trait BindingManager extends ChatActorLike
{



  var bindings = Map.empty[ActorRef,BindingManager.ChannelRequestRoomContentDateChecker]


  /**
   * This function does binding to specific channel params and
   * @param channel
   * @param request
   * @param room
   * @param content
   * @return
   */
  def doBinding(channel:String,request:String,room:String, content:JsValue):Boolean = {
    if(this.bindings.size==0) return false
    for( ( actor,fun)<-this.bindings)
    {
      if (fun(channel,request,room,content)){
        actor ! RequestInfo(channel,content,request,room)
        log.debug(s"${name}: Binding has fired for channel = $channel with request = $request and content = ${content.toString()}")

        return true
      }
    }
    false

  }

  /**
   * Operations that let you bind actors to some requests
   * @return
   */
  def receiveBindings:this.Receive= {
    case OpenBinding(name,func,actor) =>
      if(this.bindings.contains(actor))
      {
        log.error(s"$name : binding several times to the same actor")
      }
      else
      {
        this.bindings = this.bindings + (actor->func)
        actor ! ChangeBinding(this.name,"opened",self)
      }

    case CloseBinding(name,actor) =>
      this.bindings = this.bindings - actor
      actor ! ChangeBinding(this.name,"closed", self)
  }

  def parseBinding:this.ChannelRequestRoomContentDateParser = {
    case (chan,request,room, content, date) if this.doBinding(chan,request,room,content) =>
      //log.debug(s"binding works for channel,request,room")
  }
}
