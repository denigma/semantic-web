package org.denigma.actors.models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.{UUID, Calendar, Date}


case class UserStatusFree(username:String,status:String) extends UserStatusLike

//object UserStatus

trait UserStatusLike {
  val username:String
  val status:String
}


case class UserBusy(username:String) extends UserStatusLike{val status = "busy"}
case class UserJoined(username:String) extends UserStatusLike{val status = "joined"}
case class UserLeft(username:String) extends UserStatusLike{val status = "left"}
case class UserAway(username:String) extends UserStatusLike{val status = "away"}
case class UserConnected(username:String) extends UserStatusLike{val status = "connected"}
case class UserUnknown(username:String) extends UserStatusLike{val status = "unknown"}

case class UserNew(username:String) extends UserStatusLike{val status = "new"}
case class UserProhibit(username:String) extends UserStatusLike{val status = "prohibit"}
case class UserMerge(username:String) extends UserStatusLike{val status = "merge"}


trait UserStatusFormatter {

  implicit val readUserStatus: Reads[UserStatusLike] = new Reads[UserStatusLike] {
    def reads(json: JsValue): JsResult[UserStatusLike] = {
      val username = (json \ "name").as[String]

      val status:UserStatusLike = (json \ "name").asOpt[String] match {
        case None=>UserUnknown(username)
        case Some(str)=>str match
        {
          case "busy"=>UserBusy(username)
          case "joined"=>UserJoined(username)
          case "left"=>UserLeft(username)
          case "away"=>UserAway(username)
          case "connected"=>UserConnected(username)
          case "new"=>UserNew(username)
          case "merge"=>UserMerge(username)
          case "prohibit"=>UserProhibit(username)
          case status:String=>UserStatusFree(username,status)
        }
      }

      new JsSuccess[UserStatusLike](status)


    }
  }
  implicit val writeUserStatus:Writes[UserStatusLike] = new Writes[UserStatusLike] {
    def writes(o: UserStatusLike): JsValue = Json.obj("username"->o.username,"status"->o.status)
  }
  implicit val userStatusFormat: Format[UserStatusLike] = Format(readUserStatus, writeUserStatus)


}
