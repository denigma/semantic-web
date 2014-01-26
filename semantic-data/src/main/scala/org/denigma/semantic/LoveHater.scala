package org.denigma.semantic

import org.openrdf.model.impl.{StatementImpl, URIImpl}
import org.openrdf.model.{Statement, URI}
import org.openrdf.repository.RepositoryResult
import scala.collection.immutable.List
import SG._
import scala.collection.JavaConversions._

/**
 * Created by antonkulaga on 1/13/14.
 */
trait LoveHater {

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
    this.addFullRel(s"http://denigma.org/actors/resources/$sub",s"http://denigma.org/relations/resources/$rel",s"http://denigma.org/actors/resources/$obj")



}
