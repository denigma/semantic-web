package org.denigma.semantic.reading.queries


import org.denigma.semantic.reading.constructs.ConstructReader
import org.denigma.semantic.reading.selections.SelectReader
import org.denigma.semantic.reading.questions.AskReader
import org.denigma.semantic.reading.DataReader

/*
trait that provides all reading features
 */
trait UniReader extends AnyQueryReader with AskReader with DataReader with ConstructReader with SelectReader{

}
