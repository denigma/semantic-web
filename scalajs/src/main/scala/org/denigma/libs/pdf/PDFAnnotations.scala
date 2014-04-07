package org.denigma.libs.pdf

import scala.scalajs.js
import org.scalajs.dom.HTMLElement

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFAnnotations extends js.Object {
  def getData(): PDFAnnotationData = ???
  def hasHtml(): js.Boolean = ???
  def getHtmlElement(commonOjbs: js.Any): HTMLElement = ???
  def getEmptyContainer(tagName: js.String, rect: js.Array[js.Number]): HTMLElement = ???
  def isViewable(): js.Boolean = ???
  def loadResources(keys: js.Any): PDFPromise[js.Any] = ???
  def getOperatorList(evaluator: js.Any): PDFPromise[js.Any] = ???
}
