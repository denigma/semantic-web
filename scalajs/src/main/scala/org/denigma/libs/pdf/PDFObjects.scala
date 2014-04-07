package org.denigma.libs.pdf

import scala.scalajs.js

/**
 * Created by antonkulaga on 07.04.14.
 */
trait PDFObjects extends js.Object {
  def get(objId: js.Any, callback: js.Any = ???): js.Dynamic = ???
  def resolve(objId: js.Any, data: js.Any): js.Dynamic = ???
  def isResolved(objId: js.Any): js.Boolean = ???
  def hasData(objId: js.Any): js.Boolean = ???
  def getData(objId: js.Any): js.Dynamic = ???
  def clear(): Unit = ???
}
