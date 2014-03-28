package org.denigma.frontend

import org.scalajs.dom._

import org.scalajs.dom.{Attr, NamedNodeMap}
import scala.collection.mutable
import scala.scalajs.js.Undefined
import scala.scalajs.js
import org.scalajs.dom
import org.denigma.macroses.js.ClassToMap

/**
 * Useful implicit classes
 */
package object extensions extends AttributesOps with AnyJs with  RxCollectionOps