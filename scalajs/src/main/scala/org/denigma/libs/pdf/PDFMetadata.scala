package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFMetadata extends js.Object {
  def parse(): Unit = ???
  def get(name: js.String): js.String = ???
  def has(name: js.String): js.Boolean = ???
}
