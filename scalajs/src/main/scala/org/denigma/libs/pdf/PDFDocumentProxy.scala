package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFDocumentProxy extends js.Object {
  def numPages(): js.Number = ???
  def fingerprint(): js.String = ???
  def embeddedFontsUsed(): js.Boolean = ???
  def getPage(number: js.Number): PDFPromise[PDFPageProxy] = ???
  def getDestinations(): PDFPromise[js.Array[js.Any]] = ???
  def getJavaScript(): PDFPromise[js.Array[js.String]] = ???
  def getOutline(): PDFPromise[js.Array[PDFTreeNode]] = ???
  def isEncrypted(): PDFPromise[js.Boolean] = ???
  //def getData(): PDFPromise[Uint8Array] = ???
  def dataLoaded(): PDFPromise[js.Array[js.Any]] = ???
  def destroy(): Unit = ???
}
