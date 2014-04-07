package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFSource extends js.Object {
  var url: js.String = ???
  //var data: Uint8Array = ???
  var httpHeaders: js.Any = ???
  var password: js.String = ???
}
