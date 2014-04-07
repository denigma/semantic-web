package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFAnnotationData extends js.Object {
  var subtype: js.String = ???
  var rect: js.Array[js.Number] = ???
  var annotationFlags: js.Any = ???
  var color: js.Array[js.Number] = ???
  var borderWidth: js.Number = ???
  var hasAppearance: js.Boolean = ???
}
