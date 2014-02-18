package org.denigma.actors.connectors


import akka.pattern.ask

import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent.Execution.Implicits._
import concurrent.Future
import org.denigma.actors.messages._
import akka.actor.{ActorRef, ActorSystem, ActorRefFactory}
import org.denigma.actors.{Member, MainActor}


/**
 * Singletone to manage all communication stuff
 * this connector is for testing purposes only
 * use external connectors in play projects!
 */
object TestConnector  extends Connector[JsValue,JsValue,MainActor[Member]]
{
  def connect(connection:String,username:String, user: ActorRef, enumerator: Enumerator[JsValue]): (Iteratee[JsValue, Unit], Enumerator[JsValue]) =
  {
    this.currentUser = user
    //user ! Start(pusher)
    val iteratee = Iteratee.foreach[JsValue] { event =>
      user ! Received[JsValue](now,event)//ReceivedJson(now,event)
    }.mapDone { _ =>
      master ! Quit(username,connection) // this quits the channel when the web page goes away
    }
    (iteratee, enumerator)
  }


  def join(username:String, email:String, token:String, hash:String): scala.concurrent.Future[(Iteratee[JsValue,_],Enumerator[JsValue])] = {
    ( master ? Join(username, email, token, hash)).map {
      case Connected(connection:String,user:ActorRef,enumerator: Enumerator[JsValue]) =>this.connect(connection,username,user,enumerator)

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

  /*
  /**
   * @param username We ask the room actor with the username
   * @return (iteratee,enumerator) pair as a result (input and output of websocket)
   */
  def join(username:String,password:String):scala.concurrent.Future[(Iteratee[JsValue,_],Enumerator[JsValue])] =
  {

    val connection: Future[Any] = master ? SimpleJoin(username,password)
    connection.map {
      /**
       * case class that is returned contains enumerator, an Iteratee si created inside the case block
       */
      case ConnectedSimple(input: Iteratee[JsValue, _], output: Enumerator[JsValue]) => {
        (input,output)
      }


      case CannotConnect(error) =>

        // Connection error

        // A finished Iteratee sending EOF
        val iteratee = Done[JsValue,Unit]((),Input.EOF)

        // Push an error and close the socket
        val enumerator =  Enumerator[JsValue](JsObject(Seq("error" -> JsString(error)))).andThen(Enumerator.enumInput(Input.EOF))

        (iteratee,enumerator)

    }

  }
 */
  def factory: ActorRefFactory = ActorSystem("MySpec")
}
