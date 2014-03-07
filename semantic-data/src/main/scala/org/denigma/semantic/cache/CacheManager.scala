package org.denigma.semantic.cache
import akka.actor.{ActorRef, ActorSystem, Actor, Props}
import scala.concurrent.duration._
import org.denigma.semantic.controllers.{UpdateController, SimpleQueryController}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent.Akka
import org.denigma.semantic.sparql.Pat


/**
 * Class that deals with caches
 */
object UpdateWatcher extends SimpleQueryController with UpdateController
{
  val interval:FiniteDuration = 2 minutes
  //TODO: complete


}
