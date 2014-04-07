package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFPageProxy extends js.Object {
  def pageNumber(): js.Number = ???
  def rotate(): js.Number = ???
  def ref(): PDFRef = ???
  def view(): js.Array[js.Number] = ???
  def getViewport(scale: js.Number, rotate: js.Number = ???): PDFPageViewport = ???
  def getAnnotations(): PDFPromise[PDFAnnotations] = ???
  def render(params: PDFRenderParams): PDFRenderTask = ???
  def getTextContext(): PDFPromise[js.String] = ???
  def destroy(): Unit = ???
}
