package org.denigma.semantic.commons

import com.bigdata.rdf.changesets.IChangeLog
import org.denigma.semantic.writing.WriteConnection
import com.bigdata.rdf.store.AbstractTripleStore

trait ChangeWatcher {

  def apply(db:AbstractTripleStore,transaction:String,lg:LogLike): IChangeLog
}