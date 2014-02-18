package org.denigma.actors.workers

import org.denigma.actors.staff.EventActor
import org.denigma.actors.messages.{ Join}
import org.denigma.actors.models.{UserStatusLike, UserProhibit, UserMerge, UserNew}

/**
 * Check if everything is ok with auth
 * inmemory
 */
class AuthWorker extends EventActor {

  var users = List.empty[Join]

  def addUser(req:Join,list:List[Join]):UserStatusLike =
    if(list.isEmpty)
    {
      this.users = req::users
      UserNew(req.username)
    }
    else
      if(list.head.username.equals(req.username))
      {
        if(list.head.hash.equals(req.hash))
          UserMerge(req.username)
        else
          UserProhibit(req.username)
      }
      else
        this.addUser(req,list.tail)


  def receive: this.Receive = {
    case join:Join => sender ! this.addUser(join,users)
  }


}
