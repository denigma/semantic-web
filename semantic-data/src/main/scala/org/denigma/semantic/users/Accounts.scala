package org.denigma.semantic.users

import org.denigma.semantic.sparql._
import org.denigma.semantic.model.{LitStr, Trip, IRI, Quad}
import org.denigma.semantic.sparql.Pat
import scala.collection.mutable.MultiMap
import org.openrdf.model._
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.controllers.{UpdateController, WithLogger}
import org.denigma.semantic.vocabulary._
import org.denigma.semantic.cache.{UpdateInfo, PatternCache}
import scala.util.{Failure, Success, Try}
import org.denigma.semantic.sparql._
import org.openrdf.model.vocabulary.RDF
import scala.concurrent.Future

/**
* user watcher that keeps all users inmemory
*/
object Accounts extends PatternCache with WithLogger with UpdateController
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

 def userByName(name:String) =  this.userByIRI(userIRI(name))


  //TODO: improve username security
  /**
   * Turns username into IRI
   * @param name
   * @return
   */
  def userIRI(name:String): IRI = if(name.contains(":"))
    if(name.contains(WI.namespace) && !name.contains(USERS.namespace)) {
      this.lg.error(s"there was a hack attempt with username $name")
      USERS.user / name.substring(name.lastIndexOf(":")) iri
    } else IRI(name) else USERS.user / name iri



  def userByIRI(uri:IRI): Option[Account] = this.mails.get(uri).flatMap(m=>this.hashes.get(uri).map(h=>Account(uri,h,m)))


  /**
   * Provides user by its email
   * @param email
   * @return Some(user) or None
   */
  def userByEmail(email:String): Option[Account] = mails.find(kv=>kv._2==email).flatMap{
    case (key: Resource,em)=>
      hashes.get(key).map(h=>Account(key,h,em))
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

  //TODO: find better regex for email
  protected def isValidEmail(email: String): Boolean = """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined


  /**
   * Checks if it is possible to register user with such params
   * @param username
   * @param email
   * @param password
   * @return
   */
  protected def canRegister(username:String,email:String,password:String): Try[Unit] = Try {
    if(this.mails.exists(kv=>kv._2==email)) throw new Exception(s"email $email has already been registered")
    if(this.isValidEmail(email)) throw new Exception(s"email $email is not valid")
    if(this.userByName(username).isDefined) throw new Exception(s"user with username $username already exists")
    if(password.length<5) throw new Exception(s"password is too short")
    if(password.length>20) throw new Exception(s"password is too long")

    if(password.contains(username)) throw new Exception(s"password should not contain username")
  }

  def register(username:String,email:String,password:String): Future[Try[Unit]] = this.canRegister(username,email,password) match
  {
    case Failure(ex) => Future.failed(ex)
    case _ =>
      import com.github.t3hnar.bcrypt._

      val user = this.userIRI(username)
      val hash: String = password.bcrypt
      val ins = InsertQuery{
        INSERT{
          DATA(
            GRAPH(IRI(USERS.namespace),
              Trip(user,RDF.TYPE iri, USERS.classes.User),
              Trip(user, USERS.props.hasEmail, LitStr(email)),
              Trip(user,USERS.props.hasPasswordHash,LitStr(hash))
            )
          )
        }
      }
      this.insert(ins)
  }


}


case class Account(name:Resource,hash:String, email:String)

object Guest extends Account(IRI(USERS.user / "guest"),"","guest@anonimous.com")