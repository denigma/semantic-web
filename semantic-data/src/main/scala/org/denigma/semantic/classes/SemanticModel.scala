package org.denigma.semantic.classes


import org.openrdf.model._
import org.openrdf.model.BNode
import play.api.Play
import org.openrdf.model.impl.URIImpl
import org.denigma.semantic.{SG, Prefixes}
import org.openrdf.model.vocabulary
import org.denigma.semantic.data.SemanticStore
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.util._

/*
Model with statements about one particular URL
 */
abstract class SemanticModel(val url:Resource)
{

  type ModelParser = (this.type,BigdataSailRepositoryConnection,Statement, Map[Resource,SemanticModel], Int)=>Boolean

  def load(implicit db:SemanticStore):Try[Unit]= db.read[Unit](con=>this.load(con))

  /*
  loads models
   */
  def load(con:BigdataSailRepositoryConnection, path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Try[Unit] = {
    val sts = con.getStatements(url,null,null,false)
    if(!sts.hasNext) {
      play.Logger.info(s"semanticmodel resource $url not found")
      Failure(new ClassNotFoundException(s"semanticmodel resource $url not found"))
    }
    else
    {
      while(sts.hasNext){
        val st = sts.next()
        if(maxDepth!=0)  parsers.foreach{ p=>p(this,con,st,path + (this.url->this), maxDepth-1)  }
      }
     Success(Unit)
    }

  }

  /*
  functions that do some parser actions when see
   */
  var parsers:List[ModelParser] = List.empty


  var cleaners:List[(this.type)=>Unit ] = List.empty

  def clean() = this.cleaners.foreach(_(this))




}
