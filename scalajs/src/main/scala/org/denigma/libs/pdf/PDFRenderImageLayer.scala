package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFRenderImageLayer extends js.Object {
  def beginLayout(): Unit = ???
  def endLayout(): Unit = ???
  def appendImage(): Unit = ???
}
