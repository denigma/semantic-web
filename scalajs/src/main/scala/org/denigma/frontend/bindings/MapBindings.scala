package org.denigma.frontend.bindings

import rx._
import scala.collection.immutable.Map


trait MapBindings extends MapBinder{
  def maps:Map[String,Rx[Map[String, Any]]]



}

trait MapBinder {

}