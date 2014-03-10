package org.denigma.semantic.cache

import com.bigdata.rdf.spo.ISPO
import org.denigma.semantic.sparql._
import org.denigma.semantic.model.IRI
import org.openrdf.model.{URI, Statement}
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql.Pat
import org.openrdf.model.vocabulary.RDFS
import org.denigma.semantic.vocabulary.{USERS, WI}
import org.denigma.semantic.vocabulary._
import scala.collection.mutable.MultiMap
import scala.collection.{mutable, immutable}
import org.openrdf.model._
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.commons.LogLike
import org.denigma.semantic.controllers.{WithLogger, QueryController, WithSemanticReader}

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


// val isUser = pattern     Pat(?("object"),RDFS.I,?("user"),IRI(WI.USERS))




  override val name: String = "Users"

  def removeFacts(upd:UpdateInfo) = {
    val removed = this.groupByPattern(upd.removed)
    removed.get(hasEmail).foreach{ems=>
      this.mails = this.mails -- ems.map((em: ISPO) =>em.getSubject)
    }
    removed.get(hasPassword).foreach{pw=>
      this.hashes = this.hashes -- pw.map(p=>p.getSubject)
    }
  }

  def insertFacts(upd:UpdateInfo) = {
    val inserted: MultiMap[Pat, ISPO] = this.groupByPattern(upd.inserted)
    inserted.get(hasEmail).foreach{ms=>
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
}


case class User(name:URI,hash:String, email:String)
{

}