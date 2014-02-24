package org.denigma.semantic.reading.queries

import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading.questions._
import org.denigma.semantic.reading.constructs._
import org.denigma.semantic.reading._


/*
returns json to be used by sparql endpoint
 */
trait JsonQueryManager extends QueryManager[QueryResultLike] with JsonSelect with JsonAsk with JsonConstruct{

}
