package org.denigma.semantic.actors.writers

import org.denigma.semantic.actors.NamedActor
import scala.util.Try
import akka.actor.Actor
import org.denigma.rdf.sparql._

/**
 * Conditional updater
 */
trait ConditionalWriter extends WatchedWriter{
  self:NamedActor=>


  def updatesOnlyIf:Actor.Receive = {

    case InsertOnlyIf(insert,question)=> sender ! this.watchedUpdateOnlyIf(insert.stringValue,question.stringValue)

    case DeleteOnlyIf(delete,question)=> sender ! this.watchedUpdateOnlyIf(delete.stringValue,question.stringValue)

    case InsertDeleteOnlyIf(i,d,question)=> sender ! this.watchedUpdateOnlyIf(i.stringValue+" \n"+d.stringValue,question.stringValue)

    case DeleteInsertOnlyIf(d,i,question)=> sender ! this.watchedUpdateOnlyIf(d.stringValue+" \n"+i.stringValue,question.stringValue)

  }

  def updatesUnless:Actor.Receive = {

    case InsertUnless(insert,question)=> sender ! this.watchedUpdateUnless(insert.stringValue,question.stringValue)

    case DeleteUnless(delete,question)=> sender ! this.watchedUpdateUnless(delete.stringValue,question.stringValue)

    case InsertDeleteUnless(i,d,question)=> sender ! this.watchedUpdateUnless(i.stringValue+" \n"+d.stringValue,question.stringValue)

    case DeleteInsertUnless(d,i,question)=> sender ! this.watchedUpdateUnless(d.stringValue+" \n"+i.stringValue,question.stringValue)

  }



  def watchedUpdateOnlyIf(queryString:String,condition:String): Try[Boolean] = this.updateOnlyIf(queryString,condition,watcher(queryString,lg))

  def watchedUpdateUnless(queryString:String,condition:String): Try[Boolean] = this.updateUnless(queryString,condition,watcher(queryString,lg))



}
