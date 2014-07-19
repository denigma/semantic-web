package org.denigma.semantic.remote

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection
import org.denigma.semantic.reading.{QueryResultLike, SelectResult}
import org.openrdf.query.{QueryLanguage, TupleQuery, TupleQueryResult}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.sesame.{CanReadSesame, SelectReader}

import scala.util.Try

/*
sends closures that deal with read requests
 */
trait BigDataRemoteSelectReader extends CanReadRemoteBigData with SelectReader {


  override type SelectQuery = TupleQuery
  override def makeSelectQuery(con: ReadConnection, query: String)(implicit base: String): SelectQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,query,base)

}


/*
can make selects
 */
trait ISelectRemote[T] extends BigDataRemoteSelectReader{


  def select(query:String): Try[T] = this.selectQuery[T](query)(selectHandler)(WI.RESOURCE)
  protected def selectHandler:SelectHandler[T]
}

trait SimpleSelect extends ISelectRemote[TupleQueryResult]{
  override protected def selectHandler:SelectHandler[TupleQueryResult]  = (str,con,q)=>q.evaluate()
}

trait JsonSelect extends ISelectRemote[QueryResultLike]{
  override protected def selectHandler:SelectHandler[QueryResultLike]  = (str,con,q)=>SelectResult.parse(str,q.evaluate())
}
