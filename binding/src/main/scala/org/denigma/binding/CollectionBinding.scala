package org.denigma.binding

import org.scalajs.dom.{Attr, HTMLElement}
import scala.collection.mutable
import scala.scalajs.js
import rx.Rx
import org.denigma.binding.macroses.ListRxMap
import org.scalajs.dom
import org.denigma.extensions._
/**
 * Trait that provides collection binding
 */
trait CollectionBinding{

  def extractListRx[T: ListRxMap](t: T): Map[String, Rx[List[Map[String, Any]]]] =  implicitly[ListRxMap[T]].asListRxMap(t)

  def lists: Map[String, Rx[List[Map[String, Any]]]]
}