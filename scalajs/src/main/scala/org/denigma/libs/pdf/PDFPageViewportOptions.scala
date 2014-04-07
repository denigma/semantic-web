package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFPageViewportOptions extends js.Object {
  var viewBox: js.Any = ???
  var scale: js.Number = ???
  var rotation: js.Number = ???
  var offsetX: js.Number = ???
  var offsetY: js.Number = ???
  var dontFlip: js.Boolean = ???
}
