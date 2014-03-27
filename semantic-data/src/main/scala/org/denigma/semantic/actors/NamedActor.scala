package org.denigma.semantic.actors

import akka.actor.{ActorRef, Actor}
import java.util.{UUID, Calendar, Date}
import akka.event.{EventStream, SubchannelClassification}

/**
 * this is just a trait of the actor that has name
 */
trait NamedActor  extends Actor  with akka.actor.ActorLogging {


  /**
   * Name getter
   * @return    actors name
   */
  def name = self.path.name

  /**
   * Works only if it really has a parent
   */
  lazy val parent: ActorRef = context.actorFor(self.path.parent)

  /**
   * Implicit date to generate
   * @return tells about current time and date
   */
  implicit def now: Date = Calendar.getInstance().getTime()

  /**
   * Events to be executed when actor started
   */
  override def preStart() = {
    log.debug(s"$name started")
  }

  override def postRestart(reason: Throwable): Unit = {
    log.error(s"$name had to restart")
    preStart()
  }

  override def postStop(): Unit =
  {
    log.debug(s"$name stopped")
  }


  /**
   * Makes unique id
   * @return unique id
   */
  def makeId: String = UUID.randomUUID().toString()

  def bus: EventStream = context.system.eventStream


}