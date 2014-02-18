package org.denigma.actors.managers

import play.api.libs.json.{Json, JsError, JsSuccess, JsValue}
import java.util.Date
import org.denigma.actors.messages.{Push, Received}
import org.denigma.actors.models.{TaskFormatter, Message, RequestInfo, Task}
import scala.collection.immutable.SortedSet
import org.denigma.actors.orderings.DataOrdering
import org.denigma.actors.staff.ChatActorLike

/**
 * All member's task related functionality
 */
trait TaskManager extends ChatActorLike with TaskFormatter {
  protected var _tasks = SortedSet.empty[Received[Task]](new DataOrdering[Received[Task]])

  implicit def tasks = _tasks

  implicit def tasks_= (value:SortedSet[Received[Task]]): Unit = _tasks = value

  def task2Request(task:Task, request:String): RequestInfo = {
    val content: JsValue =  Json.toJson(task)(writeTask)
    RequestInfo("tasks",content,request)
  }

  /**
   * Packs task to JSON
   * @param task Task to be packed
   * @param request request with what task will be packed
   * @return JsValue (resulting JSON)
   */
  def task2Json(task:Task, request:String):JsValue = this.sendRequest2Client(task2Request(task,request))

  /**
   *  adds task
   * @param content JSON content to be parsed
   */
  def addTask(content:JsValue)(implicit date:Date):Option[Received[Task]] =
    content.validate[Task](readTask) match
    {
      case result:JsSuccess[Task] => this.addItemOnSuccess(result)(date)

      case result: JsError =>
        log.error(s"${name}: not valid task json: $content ")
        None
    }


  /**
   * Does operations with tasks
   * @param req request
   * @param content JSON content
   * @param date date when it was received
   * @return
   */
  def processTask(room:String,req:String,content:JsValue)(implicit date:Date)= req match {

    case "save" =>addTask(content)(date) match
    {
      case Some(data: Received[Task])=>this.inform(room,data.value)

      case None=> log.info(s"$name : cannot add task because it is not valid")
    }

    case  "remove" | "delete"=> this.tasks = this.getWithout(tasks,this.getId(content))


    case req => log.info(s"${name}:tasks request ' $req ' is not implemented")

  }

  def receiveTask:  this.Receive =   {

    /**
     * When you are asked to send task to the client
     */
    case Push(date, value:Task) => sendJson2Client(task2Json(value,"push"))(date)
  }

  /**
   * Parses task
   * @return
   */
  def parseTask:this.ChannelRequestRoomContentDateParser = {
    case ("tasks",req, room, content, date) => this.processTask(room,req,content)(date)

  }



}
