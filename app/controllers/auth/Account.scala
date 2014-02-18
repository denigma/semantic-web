package controllers.auth

import org.mindrot.jbcrypt.BCrypt
import org.openrdf.model.URI
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.concurrent.Future
import org.denigma.semantic.{WithSemanticPlatform, SemanticPlatform}
import com.bigdata.rdf.vocab.decls.{FOAFVocabularyDecl=>foaf}
import org.openrdf.model.impl.URIImpl

import play.api.libs.concurrent.Execution.Implicits._


case class Account(uri:URI,email: String, password: String)

case class Permission(read:Set[URI],write:Set[URI])

object Accounts extends WithSemanticPlatform{



  def authenticate(email: String, password: String)(implicit con: BigdataSailRepositoryConnection): Future[Option[Account]] =
    findByEmail(email).map{
      (aco: Option[Account]) =>aco.filter{ account => BCrypt.checkpw(password, account.password) }}


  def findByEmail(email: String)(implicit con: BigdataSailRepositoryConnection): Future[Option[Account]] = sp.db.r{
    con=>
      val mail = new URIImpl("mailto:"+email)
      val iter = con.getStatements(null,foaf.mbox,mail,false)

      ???
  }

  def findByUri(uri:URI)(implicit con: BigdataSailRepositoryConnection): Future[Option[Account]]= ???


  def create(account: Account)(implicit con: BigdataSailRepositoryConnection){

      import account._
      val pass = BCrypt.hashpw(account.password, BCrypt.gensalt())
      //insert.into(Account).values(id, email, pass, name, permission.toString)
       ???
    } //save
}