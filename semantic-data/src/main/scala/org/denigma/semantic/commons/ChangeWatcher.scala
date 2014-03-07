package org.denigma.semantic.commons

import com.bigdata.rdf.changesets.IChangeLog

trait ChangeWatcher {

  def apply(transaction:String,lg:LogLike): IChangeLog
}