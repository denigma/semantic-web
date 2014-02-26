package org.denigma.semantic.writing

import org.denigma.semantic.commons.Logged

/**
 * Created by antonkulaga on 2/24/14.
 */
trait CanWrite extends Logged{

  def writeConnection:WriteConnection
}
