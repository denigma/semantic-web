package org.denigma.semantic.actors

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.routing.{DefaultResizer, SmallestMailboxRouter}
import org.denigma.semantic.controllers.SemanticIO
import org.denigma.semantic.storage.RDFStore
import org.denigma.semantic.actors.writers.DatabaseWriter
import org.denigma.semantic.actors.readers.DatabaseReader
import org.denigma.semantic.reading.CanRead
import org.denigma.semantic.writing.CanWrite


/*
creates database actors and routers
 */
class DatabaseActorsFactory(canRead:CanRead,canWrite:CanWrite, val sys:ActorSystem,readers:(Int,Int,Int)) {


  val router = SmallestMailboxRouter(readers._2)
  val resizer = DefaultResizer(lowerBound = readers._1, upperBound = readers._3)

  protected val readerProps = Props(classOf[DatabaseReader],canRead).withRouter(router.withResizer(resizer))

  val reader = sys.actorOf(readerProps,"reader")


  protected val writerProps = Props(classOf[DatabaseWriter],canWrite)
  val writer = sys.actorOf(writerProps,"writer")

  SemanticIO.init(reader,writer)

}
