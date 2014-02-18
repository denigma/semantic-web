package org.denigma.actors.connectors


import play.api.libs.concurrent.Execution.Implicits._
import akka.actor._

import play.api.libs.json._
import play.api.libs.iteratee._


import akka.pattern.ask
import play.api.libs.iteratee.Concurrent.Channel
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import org.denigma.actors.messages._
import org.denigma.actors._


trait ChatConnector extends Connector[JsValue,JsValue,MainActor[Member]]{

  def connect(connectionName:String,username:String, user: ActorRef, enumerator: Enumerator[JsValue]): (Iteratee[JsValue, Unit], Enumerator[JsValue]) =
  {
    this.currentUser = user
    //user ! Start(pusher)
    val iteratee = Iteratee.foreach[JsValue] { event =>
      user ! Received[JsValue](now,event)//ReceivedJson(now,event)
    }.mapDone { _ =>
      master ! Quit(username,connectionName) // this quits the channel when the web page goes away
    }
    (iteratee, enumerator)
  }

  def join(username:String, email:String, token:String, hash:String): scala.concurrent.Future[(Iteratee[JsValue,_],Enumerator[JsValue])] = {

    ( master ? Join(username, email, token, hash)).map {
      case Connected(name,user:ActorRef,enumerator: Enumerator[JsValue]) =>this.connect(name,username,user,enumerator)

      case CannotConnect(error) =>{

        // Connection error

        // A finished Iteratee sending EOF
        val iteratee = Done[JsValue,Unit]((),Input.EOF)

        // Push an error and close the socket
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))

        (iteratee,enumerator)
      }
    }
  }
}
