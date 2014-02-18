package org.denigma.semantic.query.console

import scala.reflect.runtime.{universe=>ru}

import org.denigma.actors.messages._
import java.util.Date

/**
 * will be used to test queries in the console
 * */
object QueryTester extends App
{
  def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]

  var mirror = ru.runtimeMirror(this.getClass.getClassLoader)

  def receive(re:Received[_]) = re match
  {
    case v:Received[_] =>
      val t = getTypeTag(v)
      val tp: ru.Type = t.tpe

  }


  this.receive(Received(new Date(),"something"))
  this.receive(Received(new Date(),20))

}
