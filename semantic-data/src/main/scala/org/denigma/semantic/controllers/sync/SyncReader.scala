package org.denigma.semantic.controllers.sync

import org.denigma.semantic.reading._
import org.denigma.semantic.commons.LogLike
import org.denigma.semantic.writing._

/**
object that stores database of any other thing that can provide new readconnection
 */
object SyncReader extends CanRead{

  var reader:CanRead = null

  def lg = this.reader.lg

  //Read connection is just a type alias for BigDataSailConnection
  def readConnection: ReadConnection = reader.readConnection

  def readEnabled = this.reader!=null
}


trait WithSyncReader extends DataReader{

  override def lg = SyncReader.lg
  override def readConnection = SyncReader.readConnection

}