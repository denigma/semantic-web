package org.denigma.semantic.classes


import org.openrdf.model._
import org.openrdf.model.BNode
import play.api.Play
import org.openrdf.model.impl.URIImpl
import org.denigma.semantic.{SemanticPlatform, Prefixes}
import org.openrdf.model.vocabulary
import org.denigma.semantic.data.SemanticStore
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import scala.util._
import org.openrdf.repository.RepositoryResult

/*
Companion object that stores some valiable
 */
object SemanticModel{
  val defDepth = 10


}

trait TraverseParams[T<:SemanticModel] extends LoadParamsLike
{

  val model:T
  val st:Statement
  val path:Map[Resource,SemanticModel]
  val maxDepth:Int
}

case class LoadParams(con:BigdataSailRepositoryConnection,path:Map[Resource,SemanticModel] = Map.empty,maxDepth:Int = SemanticModel.defDepth) extends LoadParamsLike

/*
Params required to load SemanticModel
 */
trait LoadParamsLike
{
  val path:Map[Resource,SemanticModel]
  val maxDepth:Int
  val con:BigdataSailRepositoryConnection
}

/*
parses SemanticModel that inherits from
 */
abstract class ModelParser[T<:SemanticModel]{

  def parse: PartialFunction[TraverseParams[T],Unit]

}


/*
Model with statements about one particular URL
 */
trait SemanticModel
{
  val url:Resource

  //type ModelParser = (this.type,BigdataSailRepositoryConnection,Statement, Map[Resource,SemanticModel], Int)=>Boolean

  //type alias for traversing this SemanticModel
  type TraverseMe = TraverseParams[this.type]

  //type alias for a partial function that should parse this semanticmodel
  type CurrentModelParser = ModelParser[this.type]

  def load(implicit db:SemanticStore):Try[Unit]= db.read[Unit](con=>this.loadAll(LoadParams(con)))

  /*
  loads models
   */
//  def load(con:BigdataSailRepositoryConnection, path:Map[Resource,SemanticModel] = Map.empty, maxDepth:Int = -1): Try[Unit] = {
//    val sts: RepositoryResult[Statement] = con.getStatements(url,null,null,true)
//    if(!sts.hasNext) {
//      play.Logger.info(s"semanticmodel resource $url not found")
//      Failure(new ClassNotFoundException(s"semanticmodel resource $url not found"))
//    }
//    else
//    {
//      val np = path + (this.url->this)
//      while(sts.hasNext){
//        val st = sts.next()
//
//        if(maxDepth!=0)  parsers.foreach{ p=>p(this,con,st,np, maxDepth-1)  }
//      }
//     Success(Unit)
//    }
//
//  }


  /*
  loads all properties of the resource
   */
  def loadAll(params:LoadParamsLike):Try[Unit]



  /*
 loads models
  */
  def loadWith(sts:RepositoryResult[Statement], params:LoadParamsLike)(onParseStatement: (Statement,Map[Resource,SemanticModel])=>TraverseMe): Try[Unit] = {
    if(!sts.hasNext) {
      play.Logger.info(s"semanticmodel resource $url not found")
      Failure(new ClassNotFoundException(s"semanticmodel resource $url not found"))
    }
    else
    {
      val np = params.path + (this.url->this)
      while(sts.hasNext){
        val st: Statement = sts.next()

        if(params.maxDepth!=0)  parsers.foreach{f=>f.parse(onParseStatement(st,np))  }
      }
      Success(Unit)
    }

  }

  /*
  functions that extracts data from the database about resource
   */
  var parsers:List[CurrentModelParser] = List.empty


  var cleaners:List[(this.type)=>Unit ] = List.empty

  def clean() = this.cleaners.foreach(_(this))


}
