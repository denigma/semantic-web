package org.denigma.libs.pdf

import scala.scalajs.js
import scala.scalajs.js.annotation.JSBracketAccess

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFInfo extends js.Object {
  var PDFFormatVersion: js.String = ???
  var IsAcroFormPresent: js.Boolean = ???
  var IsXFAPresent: js.Boolean = ???
  @JSBracketAccess
  def apply(key: js.String): js.Any = ???
  @JSBracketAccess
  def update(key: js.String, v: js.Any): Unit = ???
}
