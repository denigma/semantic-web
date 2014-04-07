package org.denigma.libs.pdf

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFRenderTask extends PDFPromise[PDFPageProxy] {
  def cancel(): Unit = ???
}
