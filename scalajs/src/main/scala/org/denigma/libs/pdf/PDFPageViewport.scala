package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFPageViewport extends js.Object {
  var width: js.Number = ???
  var height: js.Number = ???
  var fontScale: js.Number = ???
  var transforms: js.Array[js.Number] = ???
  def clone(options: PDFPageViewportOptions): PDFPageViewport = ???
  def convertToViewportPoint(): js.Array[js.Number] = ???
  def convertToViewportRectangle(): js.Array[js.Number] = ???
  def convertToPdfPoint(): js.Array[js.Number] = ???
}
