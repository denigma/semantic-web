package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFTreeNode extends js.Object {
  var title: js.String = ???
  var bold: js.Boolean = ???
  var italic: js.Boolean = ???
  var color: js.Array[js.Number] = ???
  var dest: js.Any = ???
  var items: js.Array[PDFTreeNode] = ???
}
