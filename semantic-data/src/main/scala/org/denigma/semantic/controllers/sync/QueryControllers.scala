package org.denigma.semantic.controllers.sync

import org.denigma.semantic.reading.queries.{SimpleQueryManager, SemanticQueryManager}

trait SyncJsController extends WithSyncReader with SemanticQueryManager

trait SyncSimpleController extends WithSyncReader with SimpleQueryManager
