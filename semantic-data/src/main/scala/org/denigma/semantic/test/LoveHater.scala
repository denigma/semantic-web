package org.denigma.semantic.test

import org.openrdf.model.impl.{StatementImpl, URIImpl}
import org.openrdf.model.{Statement, URI}
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable.List
import scala.collection.JavaConversions._
import org.denigma.semantic.SP._
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.SP

/*
Traits for tests only
 */
trait LoveHater{


  def sub(str:String) = s"http://denigma.org/actors/resources/$str"
  def prop(rel:String) = s"http://denigma.org/relations/resources/$rel"
  def obj(str:String) = s"http://denigma.org/actors/resources/$str"


  def subject(str:String) = new URIImpl(sub(str))
  def property(str:String) = new URIImpl(prop(str))
  def predicate(str:String) = new URIImpl(obj(str))

  val Daniel = subject("Daniel")
  val Liz = subject("Liz")
  val Anton = subject("Anton")
  val RDF = predicate("RDF")
  val Immortality = predicate("Immortality")
  val loves = property("loves")
  val hates = property("hates")

  def db = SP.db


  /*
  ads some test relationships
   */
  def addTestRels() = {
    this.addRel("Daniel","loves","RDF")
    this.addRel("Anton","hates","RDF")
    this.addRel("Daniel","loves","Immortality")
    this.addRel("Liz","loves","Immortality")
    this.addRel("Anton","loves","Immortality")
    this.addRel("Ilia","loves","Immortality")
    this.addRel("Edouard","loves","Immortality")
  }


  /*
just a function for testing
*/
  def addFullRel(sub:String,rel:String,obj:String) = {
    val s: URIImpl = new URIImpl(sub)
    val p: URIImpl = new URIImpl(rel)
    val o: URIImpl = new URIImpl(obj)

    db.write{
      implicit con=>
        val st = new StatementImpl(s, p, o)
        con.add(st)
    }
  }

  def readCon: BigdataSailRepositoryConnection = db.repo.getReadOnlyConnection
  def writeCon: BigdataSailRepositoryConnection = db.repo.getUnisolatedConnection
  /*
  reads relationship from the repository
   */
  def getRel(rel:URI) = {
    db.read{
      implicit r=>
        val iter: RepositoryResult[Statement] = r.getStatements(null,rel,null,true)
        iter.asList().toList
    }.getOrElse(List.empty)
  }

  /*
just a function for testing
 */
  def addRel(sub:String,rel:String,obj:String) =
    this.addFullRel(this.sub(sub),this.prop(rel),this.obj(obj))



}
