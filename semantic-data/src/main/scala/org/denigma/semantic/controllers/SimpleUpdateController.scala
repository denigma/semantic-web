package org.denigma.semantic.controllers

import org.denigma.semantic.controllers.writers.{UpdateController, DataWriter}
import akka.util.Timeout
import scala.concurrent.duration._
import com.bigdata.rdf.sail.{BigdataSailUpdate, BigdataSailRepositoryConnection}

trait  SimpleUpdateController extends UpdateController[Unit] with SemanticWriter{
  override val writeTimeout: Timeout = Timeout(5 seconds)

  override val updateHandler =  _updateHandler _

  /*
handler to make  select
*/
  protected def _updateHandler(query: String, con: BigdataSailRepositoryConnection, q: BigdataSailUpdate): Unit = q.execute()
 
}
