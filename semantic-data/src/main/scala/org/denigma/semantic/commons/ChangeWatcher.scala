package org.denigma.semantic.commons

import com.bigdata.rdf.changesets.IChangeLog
import org.scalax.semweb.commons.LogLike

trait ChangeWatcher {

  def apply(transaction:String,lg:LogLike): IChangeLog
}