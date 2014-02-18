package org.denigma.actors

import akka.actor._

import play.api.libs.iteratee._
import play.api.libs.iteratee.Concurrent._


import org.denigma.actors.messages._



import scala.Some
import scala.reflect.ClassTag
import org.denigma.actors.staff.{NamedActor, CreativeActor}
import org.denigma.actors.rooms.{ChatRoomActor, MemberHolder, RoomActor}
import akka.testkit.TestActorRef
import org.denigma.actors.workers.AuthWorker
import scala.concurrent.duration._
import org.denigma.actors.messages.{Suicide, Received}
import org.denigma.actors.models._
import play.api.libs.json.{Json, JsValue}
import akka.actor.ActorRef
import org.denigma.actors.rooms.messages.{LeaveRoom, JoinRoom}
import akka.pattern.ask
import akka.util.Timeout
import scala.util.matching.Regex

/**
 * Main actor is the actor that supervises (in akka terminology) and contains all other actors inside.
 *  What it does:
 *  establishes the connection
 *  creates and supervises (in akka's meaning of this term) member actors
 *  routes messages between actors (for instance with tellAll it broadcast chat messages and tasks to all members)
 *  subscribes members and other actors to events with a help of MessageBus object
 *
 * It contains::
 *  members map where all member actors (see later) are stored as well as rooms
 *  different worker actors that are responsible for different parts of the functionality
 *
 *
 * @tparam T type of member actors that are inside of it
 */
class MainActor[T<:Member:ClassTag] extends CreativeActor with MemberHolder
{
  var rooms:Map[String,ActorRef] = Map.empty[String,ActorRef]

  var authWorker:ActorRef = null
  implicit val timeout = Timeout(1 seconds)

  def factory: ActorRefFactory = this.context



  /**
   * Adds a member actor to the list
   * @param username name of the participant
   * @return
   */
  def addMember(username:String):ActorRef =
  {
    val user = this.createActor[T](username) //context.actorOf(Props[T], name=username)
    members+=username->user
    this.onAddMember(user)
    user
  }

  /**
   * Fires when ne member is added
   * @param member user actor to be added
   */
  def onAddMember(member:ActorRef)
  {
    member ! JoinRoom(this.defaultRoom._1,this.defaultRoom._2)
  }

  var defaultRoom:(String,ActorRef) = (null,null)


  def initAuth() = {
    this.authWorker=  this.createActor[AuthWorker]("auth")
    authWorker
  }


  override def preStart() = {
    log.debug(s"$name STARTED")
    this.initAuth()
    this.addRoom[ChatRoomActor]("all")
    this.defaultRoom = this.rooms.head
  }


  /**
   * Adds bot to the chat room. Bot has same properties as normal member
   * but does not have a client to connect to
   * @param username bot name
   * @param factory factory function that creates robot
   * @return
   */
  def addBot[TA<:NamedActor:ClassTag](username:String,factory: ()=>TA):ActorRef =
  {
    members.get(username) match
    {
      case Some(user:ActorRef)=> return user
      case None=>
    }
    val bot: ActorRef = createActor[TA](username,factory)
    members+=username->bot
    bot
  }
  /**
   * Authorisatin handler, forwards authorisation to auth actor
   * @param join
   */
  def joinHandler(join:Join) =
  {
    val username:String = join.username
    val resp = sender
    val perm = (this.authWorker ? join).mapTo[UserStatusLike]
    perm.onSuccess{
      case UserProhibit(name) =>
        val err = s"The username $username is already used"
        log.error(err)
        resp ! CannotConnect(err)

      case UserNew(username) if this.allowName(join.username) =>
        val user: ActorRef = this.addMember(username)
        log.info(s"user with $username was just added")

        startUser(user, resp)

      case UserMerge(username) if this.allowName(join.username) =>
        val user = this.members.get(username).get
        log.info(s"user with $username received new connection that is MERGED")
        startUser(user, resp)


      case _ => this.log.error(s"$name : unknown auth respond")

    }
    perm.onFailure{
      case _ =>
        val err = s"The username $username had troubles with auth"
        this.log.error(err)
    }
  }


  /**
   * Sends connection info to the user
   * @param user
   * @param resp
   */
  def startUser(user: ActorRef, resp: ActorRef)
  {
    val (enum: Enumerator[JsValue], pusher: Channel[JsValue]) = Concurrent.broadcast[JsValue]
    val con = this.makeId

    user ! Start(pusher, con)

    resp ! Connected(con, user, enum)
  }


  def receiveConnection:this.Receive =
  {
    /**
     * Creates bot with factory function that was received
     */
    case Bot(name,factory) =>  this.addBot(name,factory)


     /**
     * Users login, email, token and passwords hash achieved by application
     */
    case join:Join => this.joinHandler(join)



    /**
     * UserStatus closes websocket connection
     */
    case quit @ Quit(username, connection) =>
      val user = members.get(username).get
      val fut = user ? quit
      fut.onSuccess{
        case true =>
          removeMember(username)
          context.stop(user)
          this.log.debug(s"$name: user called $username quited")
        case false =>this.log.debug(s"$name: user called $username disconnected from one of the clients")
      }

    case Suicide(name,value,actor) =>
      this.log.error(value)
      context.stop(actor)
  }






  /** *
    * Input of room actor
    * Room actor works only with websocket request and opens/closes socket connections when needed
    * @return
    */
  def receive = receiveConnection orElse this.tellUser[JsValue]


  /**
   * removes users from rooms
   * @param username user to be removed
   */
  def removeMember(username:String)=
  {
    members.get(username) match
    {
      case Some(user)=>
        this.rooms.foreach{case (key,value)=> user ! LeaveRoom(key,value)}
        members = members - username
      case None=> this.log.error("$name : deleting a user $username that is not inside a room")
    }
    //simpleRooms =  simpleRooms.map{case (key,value)=>(key,value-username)}
  }

  def addRoom[T<:RoomActor:ClassTag](roomName:String): ActorRef ={
    val room = this.createActor[T](roomName)
    this.rooms = this.rooms + (roomName->room)
    room
  }

  /**
   * Creates test room
   * @param roomName
   * @tparam T
   * @return
   */
  def addTestRoom[T<:RoomActor:ClassTag](roomName:String): TestActorRef[T] =
  {
    val room: TestActorRef[T] = this.createTestActor[T](roomName)(this.context.system)
    this.rooms = this.rooms + (roomName->room)
    room
  }

  def add2Room[T<:RoomActor:ClassTag](userName:String,roomName:String):Unit =  if(this.allowName(roomName) && this.allowName(userName))
  {
    this.members.get(userName) match
    {
      case Some(actor)=> this.add2Room[T](userName,actor,roomName)
      case None=> this.log.error(s"$name : cannot find user with username = $userName")
    }
  }

  def add2Room[T<:RoomActor:ClassTag](userName:String,actor:ActorRef,roomName:String):Unit=  if(this.allowName(roomName) && this.allowName(userName))
  {
    val roomActor = this.rooms.getOrElse(roomName,this.addRoom(roomName))
    actor ! JoinRoom(roomName,roomActor)
  }

}