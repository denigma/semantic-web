package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFJSStatic extends js.Object {
  var maxImageSize: js.Number = ???
  var disableFontFace: js.Boolean = ???
  def getDocument(source: js.String, pdfDataRangeTransport: js.Any = ???, passwordCallback: js.Function2[js.Function1[js.String, Unit], js.String, js.String] = ???, progressCallback: js.Function1[PDFProgressData, Unit] = ???): PDFPromise[PDFDocumentProxy] = ???
}
