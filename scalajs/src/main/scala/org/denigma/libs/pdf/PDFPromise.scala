package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFPromise[T] extends js.Object {
  def isResolved(): js.Boolean = ???
  def isRejected(): js.Boolean = ???
  def resolve(value: T): Unit = ???
  def reject(reason: js.String): Unit = ???
  def then(onResolve: js.Function1[T, Unit], onReject: js.Function1[js.String, Unit] = ???): PDFPromise[T] = ???
}
