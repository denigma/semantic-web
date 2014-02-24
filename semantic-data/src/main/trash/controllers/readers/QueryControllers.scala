package org.denigma.semantic.controllers.readers

import com.bigdata.rdf.sail._
import scala.concurrent.Future
import scala.util.Try
import org.openrdf.model.Statement
import org.denigma.semantic.data._
import org.denigma.semantic.actors.Data
import scala.reflect._
import reflect.ClassTag
import org.denigma.semantic.reading.quries._
import org.denigma.semantic.commons.WI

trait SelectController[TOut] extends Selector {

  /*
  needed to avoid type erasure for futures
   */


  /*
handler to make  select
*/
  val selectHandler:TupleQuering[TOut]
  /*
   select without any modifications
   */
  def selectQuery(query:String): Future[Try[TOut]] = select[TOut](query,selectHandler,WI.RESOURCE)

}


trait AskController[Out] extends Asker {
  val askHandler:AskQuering[Out]
  /*
just  ask query without any modification
 */
  def askQuery(query:String): Future[Try[Out]] = question[Out](query,askHandler, WI.RESOURCE)
}

trait ConstructController[T] extends Constructor {
  val constructHandler:GraphQuering[T]

  protected def _constructHandler(query:String,con: BigdataSailRepositoryConnection, q:BigdataSailGraphQuery): T
  def constructQuery(query:String): Future[Try[T]] = this.construct[T](query,constructHandler,WI.RESOURCE)

}

/*
controller that does any queries (quries which type will be determined further)
 */
trait AnyQueryController[T] extends AnyQueryMaker with AskController[T] with ConstructController[T] with SelectController[T]{

  val anyQueryHandler:AnyQuering[T] = {
    case (query:String,con:BigdataSailRepositoryConnection,q:BigdataSailTupleQuery)=>
      this.selectHandler(query,con,q)
    case (query:String,con:BigdataSailRepositoryConnection,q:BigdataSailGraphQuery)=>
      this.constructHandler(query,con,q)

    case (query:String,con:BigdataSailRepositoryConnection,q:BigdataSailBooleanQuery)=>
      this.askHandler(query,con,q)
  }

  def query(str:String): Future[Try[T]] = this.anyQuery[T](str,anyQueryHandler,WI.RESOURCE)

}