package org.denigma.semantic.writing

import org.denigma.semantic.commons.Logged

/**
Trait that can provide writeConnection. It is used everywhere where we need to write something into the database
 */
trait CanWrite extends Logged{

  def writeConnection:WriteConnection
}
