package org.denigma.semantic.users

import org.denigma.semantic.sparql._
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql.Pat
import scala.collection.mutable.MultiMap
import org.openrdf.model._
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.controllers.WithLogger
import org.denigma.semantic.vocabulary._
import org.denigma.semantic.model.Quad
import org.denigma.semantic.cache.{UpdateInfo, PatternCache}
import scala.util.{Success, Try}
import akka.actor.FSM.Failure

/**
* user watcher
*/
object Users extends PatternCache with WithLogger
{


  var mails= Map.empty[Resource,String]
  var hashes = Map.empty[Resource,String]


  override var patterns = Set.empty[Pat]

  val hasEmail = this pattern Pat(?("user"),USERS.props.hasEmail,?("email"), USERS.namespace iri)
  val hasPassword = this pattern Pat(?("user"),USERS.props.hasPasswordHash,?("password"), USERS.namespace iri)

  override val name: String = "Users"

  def removeFacts(upd:UpdateInfo) = {
    val removed = this.groupByPattern(upd.removed)
    removed.get(hasEmail).foreach{ems=>
      this.mails = this.mails -- ems.map((em) =>em.getSubject)
    }
    removed.get(hasPassword).foreach{pw=>
      this.hashes = this.hashes -- pw.map(p=>p.getSubject)
    }
  }

  /**
   * Inserts facts about user
   * @param upd
   */
  def insertFacts(upd:UpdateInfo) = {
    val inserted: MultiMap[Pat, Quad] = this.groupByPattern(upd.inserted)
    inserted.get(hasEmail).foreach{
      ms=>
      this.mails = this.mails ++ ms.map(m=>(m.getSubject,m.getObject.stringValue()))
    }
    inserted.get(hasPassword).foreach{ms=>
      this.hashes= this.hashes ++ ms.map(m=>(m.getSubject,m.getObject.stringValue()))
    }
  }

  override def updateHandler(upd: UpdateInfo): Unit = {
    this.removeFacts(upd)
    this.insertFacts(upd)
  }



  override def onResult(p: PatternResult): Unit = {
    val res: Map[Pat, Set[Statement]] = p.results

    res.get(hasEmail).foreach{ms=>
      this.mails = this.mails ++ ms.map(m=>(m.getSubject,m.getObject.stringValue()))
    }
    res.get(hasPassword).foreach{ms=>
      this.hashes= this.hashes ++ ms.map(m=>(m.getSubject,m.getObject.stringValue()))
    }


  }


  /**
   * Provides user by his name
   * @param name
   * @param prefix
   * @return
   */
  def userByName(name:String,prefix:String = USERS.namespace) = IRI(prefix / name) match {
    case uri=>this.mails.get(uri).flatMap(m=>this.hashes.get(uri).map(h=>User(uri,h,m)))
  }


  /**
   * Provides user by its email
   * @param email
   * @return Some(user) or None
   */
  def userByEmail(email:String): Option[User] = mails.find(kv=>kv._2==email).flatMap{
    case (key: Resource,em)=>
      hashes.get(key).map(h=>User(key,h,em))
  }

  /**
   * Authentificates the user
   * @param username username
   * @param password password
   * @return
   */
  def auth(username:String,password:String):Try[Unit] = {
    Try {
      import com.github.t3hnar.bcrypt._
      this.userByName(username).map{user=>
        if(!password.isBcrypted(user.hash)) new Exception(s"password does not match for $username !")
    }.getOrElse(throw new Exception(s"user $username not found"))
    }
  }
  def authByEmail(email:String,password:String):Try[Unit] = {
    Try {
      import com.github.t3hnar.bcrypt._
      this.userByEmail(email).map{user=>
        if(!password.isBcrypted(user.hash)) new Exception(s"password does not match for $email !")
      }.getOrElse(throw new Exception(s"user with email $email not found"))
    }
  }

}


case class User(name:Resource,hash:String, email:String)