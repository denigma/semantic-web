package org.denigma.semantic.reading

import org.denigma.semantic.commons.Logged

/*
just a trait
 */
trait CanRead extends Logged {


  def readConnection: ReadConnection

}
