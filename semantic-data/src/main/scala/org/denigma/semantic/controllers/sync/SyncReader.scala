package org.denigma.semantic.controllers.sync

import org.denigma.semantic.reading._
import org.denigma.semantic.commons.LogLike
import org.denigma.semantic.writing._

object SyncReader extends CanRead{

  var reader:CanRead = null

  def lg = this.reader.lg
  def readConnection: ReadConnection = reader.readConnection

  def readEnabled = this.reader!=null
}


trait WithSyncReader extends CanRead{

  override def lg = SyncReader.lg
  override def readConnection = SyncReader.readConnection

}