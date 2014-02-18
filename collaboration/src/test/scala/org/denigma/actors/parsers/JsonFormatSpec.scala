package org.denigma.parsers
import org.denigma.actors.models._
import org.denigma.actors.models.Message
import org.denigma.actors.models.RequestInfo
import org.denigma.actors.models.Task

//important as it contains implicits
import play.api.libs.json._


import org.scalatest._

object Formats extends RequestInfoFormatter with MessageFormatter with TaskFormatter with SearchFormatter


/**
 * This spec test json serialization
 */
class JsonFormatSpec extends WordSpec with ShouldMatchers {

  import Formats._


  "Format" should{


    var req:RequestInfo = null
    var message:Message = null

    val messageRequest = Json.obj(
      "channel" -> "messages",
      "content" -> Json.obj(
        "id" -> "someID",
        "user" -> "bob",
        "text" -> "My message is very long"
      ),
      "request"->"save")

    "Read requestInfo from JSON" in {
        val res: JsResult[RequestInfo] = messageRequest.validate[RequestInfo](Formats.requestInfoFormat)
        res match
        {
          case result:JsSuccess[RequestInfo] =>   req = result.value
          case _ => this.fail("Bad parsing")
        }

      req.channel.shouldEqual("messages")
      req.request.shouldEqual("save")

    }

    "Write requestInfo from JSON" in {
      val task = Task("someID","user1","title1",true)
      val ri = RequestInfo("tasks",Json.toJson(task)(Formats.writeTask),"push")
      val res: JsResult[RequestInfo] = Json.toJson(ri).validate[RequestInfo]
      res match
      {
        case result:JsSuccess[RequestInfo] =>
          ri.shouldEqual(result.value)
          ri.content.validate[Task] match
          {
            case res:JsSuccess[Task]=>res.value.shouldEqual(task)
            case _=>this.fail("Bad task parsing inside a request")
          }

        case _ => this.fail("Bad request info parsing")
      }

    }



    "Read message from JSON" in {
      val res: JsResult[Message] = req.content.validate[Message](Formats.messageFormat)
      res match
      {
        case result:JsSuccess[Message] =>   message = result.value
        case _ => this.fail("Bad message parsing")
      }

      message.id.shouldEqual("someID")
      message.user.shouldEqual("bob")
      message.text.shouldEqual("My message is very long")

    }

    "Write tasks to JSON" in {
      val task = Task("someID","user1","title1",true)
      val jsTask = Json.toJson(task)
      val res: JsResult[Task] = jsTask.validate[Task]
      res match
      {
        case result:JsSuccess[Task] => task.shouldEqual(result.value)
        case _ => this.fail("Bad task parsing")
      }
    }
  }


}
