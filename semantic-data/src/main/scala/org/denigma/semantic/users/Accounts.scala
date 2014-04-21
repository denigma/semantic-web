package org.denigma.semantic.users

import scala.collection.mutable.MultiMap
import org.denigma.semantic.controllers.{UpdateController, WithLogger}

import scala.util.Try
import scala.concurrent.Future
import org.denigma.semantic.actors.cache.PatternCache
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.IRI
import scala.util.Failure
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.scalax.semweb.rdf.Quad
import org.denigma.semantic.actors.cache.Cache.UpdateInfo
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sparql._
import org.scalax.semweb.rdf.vocabulary._
/**
* user watcher that keeps all users inmemory
*/
object Accounts extends PatternCache with WithLogger with UpdateController
{

  var mails= Map.empty[Res,String]
  var hashes = Map.empty[Res,String]


  override var patterns = Set.empty[Pat]

  val hasEmail = this pattern Pat(?("user"),USERS.props.hasEmail,?("email"), USERS.namespace iri)
  val hasPassword = this pattern Pat(?("user"),USERS.props.hasPasswordHash,?("password"), USERS.namespace iri)

  override val cacheName: String = "Users"

  def removeFacts(upd:UpdateInfo) = {
    val removed = this.groupByPattern(upd.removed)
    removed.get(hasEmail).foreach{ems=>
      this.mails = this.mails -- ems.map((em) =>em.s)
    }
    removed.get(hasPassword).foreach{pw=>
      this.hashes = this.hashes -- pw.map(p=>p.s)
    }
  }

  /**
   * Inserts facts about user
   * @param upd
   */
  def insertFacts(upd:UpdateInfo) = {

    //lg.error(upd.inserted.toString())

    val inserted: MultiMap[Pat, Quad] = this.groupByPattern(upd.inserted)
    inserted.get(hasEmail).foreach{
      ms=>
      this.mails = this.mails ++ ms.map(m=>(m.s,m.o.label))
    }
    inserted.get(hasPassword).foreach{ms=>
      this.hashes= this.hashes ++ ms.map(m=>(m.s,m.o.label))
    }
  }

  override def updateHandler(upd: UpdateInfo): Unit = {
    this.removeFacts(upd)
    this.insertFacts(upd)
  }



  override def onResult(p: PatternResult): Unit = {
    val res: Map[Pat, Set[Quad]] = p.results

    res.get(hasEmail).foreach{ms=>
      this.mails = this.mails ++ ms.map(m=>(m.s,m.o.label))
    }
    res.get(hasPassword).foreach{ms=>
      this.hashes= this.hashes ++ ms.map(m=>(m.s,m.o.label))
    }


  }

 def userByName(name:String): Option[Account] =  this.userByIRI(userIRI(name))


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
    case (key,em)=>
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
      this.userByName(username).map{
        user=>  if(!password.isBcrypted(user.hash)) throw WrongPasswordForUser(password,username)
    }.getOrElse{
        val user = userIRI(username) //maybe used further
        throw UserNotFound(username)
      }
    }
  }

  def authByEmail(email:String,password:String):Try[Unit] = {
    Try {
      import com.github.t3hnar.bcrypt._
      this.userByEmail(email).map{user=>
        if(!password.isBcrypted(user.hash)) throw WrongPasswordForEmail(email,password)
      }.getOrElse(throw EmailNotFound(email))
    }
  }

  //TODO: find better regex for email
  protected def isValidEmail(email: String): Boolean = """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined


  /**
   * validates params before sending to the database
   * @param username
   * @param email
   * @param password
   * @return
   */
  protected def canRegister(username:String,email:String,password:String):Try[Boolean] = Try{
    if(this.mails.exists(kv=>kv._2==email)) throw  EmailAlreadyRegisteredException(email)
    if(!this.isValidEmail(email)) throw   EmailNotValidException(email)
    if(this.userByName(username).isDefined) throw UserAlreadyRegisteredException(username)
    if(password.length<5) throw PasswordTooShortException(password)
    if(password.length>20) throw PasswordTooLongException(password)
    if(password.contains(username) ) throw  PasswordTooSimpleException(password)
    true
  }

  /**
   * Registers a user in the database
   * @param username username
   * @param email user email
   * @param password user password
   * @return Future[Try[Boolean]]
   */
  def register(username:String,email:String,password:String): Future[Try[Boolean]] =  this.canRegister(username,email,password) match
  {
    case f@ Failure(tw)=>Future.successful(f)
    case _=>
      import com.github.t3hnar.bcrypt._

      val user: IRI = this.userIRI(username)
      val hash: String = password.bcrypt

      val ins =
       INSERT{
        DATA(
          GRAPH(IRI(USERS.namespace),
            Trip(user,RDF.TYPE, USERS.classes.User),
            Trip(user, USERS.props.hasEmail, StringLiteral(email)),
            Trip(user,USERS.props.hasPasswordHash, StringLiteral(hash))
          )
        )
      }

      val cond = ASK (
        Br(
          Pat(user,RDF.TYPE, USERS.classes.User)
        ) UNION Br(
          Pat(?("anyuser"),USERS.props.hasEmail,StringLiteral(email)),
          Pat(?("anyuser"),RDF.TYPE, USERS.classes.User)
        )
      )

      val insUnless = InsertUnless(ins,cond)
      //lg.debug("INSERT ="+insUnless.insert.stringValue)
     // lg.debug("UNLESS ="+ insUnless.question.stringValue)

      this.insertUnless(insUnless)
  }


  case class EmailAlreadyRegisteredException(email:String) extends Exception(s"email $email has already been registered!")
  case class EmailNotValidException(email:String) extends Exception(s"email $email is not valid!")
  case class UserAlreadyRegisteredException(user:String) extends Exception(s"email $user has already been registered!")
  case class PasswordTooShortException(password:String) extends Exception(s"password $password is too short!")
  case class PasswordTooLongException(password:String) extends Exception(s"password $password is too long!")
  case class PasswordTooSimpleException(password:String) extends Exception(s"password $password is too simple!")
  case class WrongPasswordForEmail(password:String,email:String) extends Exception(s"password $password for email $email is wrong!")
  case class WrongPasswordForUser(password:String,user:String) extends Exception(s"password $password for user $user is wrong!")
  case class UserNotFound(user:String) extends Exception(s"User $user was not found!")
  case class EmailNotFound(email:String) extends Exception(s"User for $email was not found!")





}


case class Account(name:Res,hash:String, email:String)

object Guest extends Account(IRI(USERS.user / "guest"),"","guest@anonimous.com")