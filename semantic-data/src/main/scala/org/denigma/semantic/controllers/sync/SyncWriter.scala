package org.denigma.semantic.controllers.sync

import org.denigma.semantic.writing._

/**
object that stores database of any other thing that can provide new writeconnection
  */
object SyncWriter extends CanWrite{

  var writer:CanWrite = null

  def lg = this.writer.lg

  //Write connection is just a type alias for BigDataSailConnection
  def writeConnection: WriteConnection = writer.writeConnection

  def writeEnabled = this.writer!=null


}

trait WithSyncWriter extends DataWriter{

  override def lg = SyncWriter.lg
  override def writeConnection = SyncWriter.writeConnection


}