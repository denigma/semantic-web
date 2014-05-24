package org.denigma.semantic.reading

import org.scalax.semweb.sesame.ConstructReader
import com.bigdata.rdf.sail.BigdataSailGraphQuery
import org.openrdf.query.{GraphQueryResult, QueryLanguage}
import scala.collection.immutable._
import scala.collection.JavaConversions._
import scala.Vector
import play.api.libs.json.{Json, JsValue}
import org.scalax.semweb.rdf.vocabulary.WI


/*
sends closure that deal with construct requests
 */
trait BigDataConstructReader extends ConstructReader with CanReadBigData {
  type ConstructQuery = BigdataSailGraphQuery

  override def makeConstructQuery(con: ReadConnection, query: String)(implicit base: String): ConstructQuery = con.prepareGraphQuery(QueryLanguage.SPARQL,query,base)
}


/*
result of CONSTRUCT SPARQL query
 */
case class ConstructResult(query:String,quads:Vector[JsValue]) extends QueryResultLike
{

  //  def toProp(kv:(String,String)): JsObject = Json.obj("name"->kv._1,"value"->kv._2,"id"->kv.hashCode())
  //  def toProps(mp:Map[String,String]): JsValue = Json.obj("id"->mp.hashCode(),"properties"->mp.map(toProp).toList)
  lazy val asJson:JsValue = Json.obj("query"->query,"results" -> Json.obj("quads"->Json.toJson(quads)))

}

object ConstructResult {

  def parse(query:String,results:GraphQueryResult):ConstructResult = {
    val builder =  Vector.newBuilder[JsValue]
    while(results.hasNext){
      builder.+=(Json.obj())
    }
    val v = builder.result()
    ConstructResult(query,v)
  }
}


/*
interface that deals with construct quiries
 */
trait IConstruct[T] extends BigDataConstructReader {

  protected def constructHandler:ConstructHandler[T]
  def construct(query:String) = this.graphQuery(query)(constructHandler)(WI.RESOURCE)

}

trait SimpleConstruct extends IConstruct[GraphQueryResult] {
  override protected def constructHandler:ConstructHandler[GraphQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonConstruct extends IConstruct[QueryResultLike] {

  override protected def constructHandler:ConstructHandler[QueryResultLike] = (str,con,q)=>ConstructResult.parse(str,q.evaluate())
}