package org.denigma.libs.pdf

import scala.scalajs.js
import org.scalajs.dom.CanvasRenderingContext2D

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFRenderParams extends js.Object {
  var canvasContext: CanvasRenderingContext2D = ???
  var textLayer: PDFRenderTextLayer = ???
  var imageLayer: PDFRenderImageLayer = ???
  var continueCallback: js.Function1[js.Function0[Unit], Unit] = ???
}
