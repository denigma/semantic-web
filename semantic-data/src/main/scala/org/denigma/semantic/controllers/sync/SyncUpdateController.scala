package org.denigma.semantic.controllers.sync

import org.denigma.semantic.writing.Updater
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.commons.LogLike

trait SyncUpdateController extends Updater with WithSyncWriter {

}