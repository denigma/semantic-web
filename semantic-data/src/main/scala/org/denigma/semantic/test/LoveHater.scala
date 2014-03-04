package org.denigma.semantic.test

import org.openrdf.model.impl.{StatementImpl}
import org.openrdf.model.{Statement, URI}
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable.List
import scala.collection.JavaConversions._
import org.denigma.semantic.controllers.sync._
import org.denigma.semantic.model.IRI

/**
Traits for tests only
 */
trait LoveHater extends WithSyncReader with SyncUpdateController{


  def sub(str:String) = s"http://denigma.org/actors/resources/$str"
  def prop(rel:String) = s"http://denigma.org/relations/resources/$rel"
  def obj(str:String) = s"http://denigma.org/actors/resources/$str"


  def subject(str:String) = IRI(sub(str))
  def property(str:String) = IRI(prop(str))
  def predicate(str:String) = IRI(obj(str))

  val Daniel = subject("Daniel")
  val Liz = subject("Liz")
  val Anton = subject("Anton")
  val RDF = predicate("RDF")
  val Immortality = predicate("Immortality")
  val loves = property("loves")
  val hates = property("hates")



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
    val s = IRI(sub)
    val p = IRI(rel)
    val o= IRI(obj)

    write{
      implicit con=>
        val st = new StatementImpl(s, p, o)
        con.add(st)
    }
  }

  /*
  reads relationship from the repository
   */
  def getRel(rel:URI) = {
    read{
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
