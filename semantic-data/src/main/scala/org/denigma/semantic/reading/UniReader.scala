package org.denigma.semantic.reading

import org.denigma.semantic.reading.queries.AnyQueryReader

/*
trait that provides all reading features
 */
trait UniReader extends CanReadBigData with AnyQueryReader with BigDataAskReader with DataReader with BigDataConstructReader with BigDataSelectReader{

}
