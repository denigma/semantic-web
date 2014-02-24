package org.denigma.semantic.controllers.writers

import org.denigma.semantic.controllers.writers._
import scala.reflect.ClassTag
import org.denigma.semantic.data._
import org.denigma.semantic.WI
import scala.concurrent.Future
import scala.util.Try
import org.denigma.semantic.actors.Data
import com.bigdata.rdf.sail.{BigdataSailUpdate, BigdataSailRepositoryConnection, BigdataSailTupleQuery}


/*
Update controller processes string update requests
 */
trait UpdateController[T] extends Updater {

  val updateHandler = _updateHandler _

  /*
handler to make  select
*/
  protected def _updateHandler(query:String,con: BigdataSailRepositoryConnection, q:BigdataSailUpdate): T
  /*
   select without any modifications
   */
  def updateQuery(query:String): Future[Try[T]] = this.update[T](query,updateHandler,WI.RESOURCE)
}
