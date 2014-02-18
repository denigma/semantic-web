package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._


// imports only Reads implicits
// imports only Writes implicits


/**
 * Task class that contains a task assingned by somebody
 * @param owner owner of the task, who does it
 * @param title title of the task
 * @param completed completion mark
 */
case class Task(id:String,owner:String, title:String, completed:Boolean = false)   extends IdOwner
//TODO: rename owner to user in both frontend and backend

trait TaskFormatter {
  implicit val readTask: Reads[Task] =  ((__ \ "id").read[String] ~(__ \ "owner").read[String] ~ (__ \ "title").read[String] ~ (__ \ "completed").read[Boolean])(Task)
  implicit val writeTask:Writes[Task] = (
    (__ \ "id").write[String]
      ~ (__ \ "owner").write[String]
      ~ (__ \ "title").write[String]
      ~ (__ \ "completed").write[Boolean])(unlift(Task.unapply))

  implicit val taskFormat: Format[Task] = Format(readTask, writeTask)


}



