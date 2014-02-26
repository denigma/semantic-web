package org.denigma.semantic.controllers.sync

import org.denigma.semantic.writing._
import org.denigma.semantic.commons.LogLike


object SyncWriter extends CanWrite{

  var writer:CanWrite = null

  def lg = this.writer.lg
  def writeConnection: WriteConnection = writer.writeConnection

  def writeEnabled = this.writer!=null
}


trait WithSyncWriter extends CanWrite{

  override def lg = SyncWriter.lg
  override def writeConnection = SyncWriter.writeConnection

}