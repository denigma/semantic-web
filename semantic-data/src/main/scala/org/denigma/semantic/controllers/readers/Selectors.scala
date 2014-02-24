package org.denigma.semantic.controllers.readers

import scala.reflect.ClassTag
import org.denigma.semantic.data._
import org.denigma.semantic.WI
import scala.concurrent.Future
import scala.util.Try
import org.denigma.semantic.actors.Data
import akka.pattern.ask
import com.bigdata.rdf.sail.{BigdataSailBooleanQuery, BigdataSailTupleQuery, BigdataSailRepositoryConnection}
import org.openrdf.model.Value
import reflect.ClassTag



trait Selector extends DataReader {

  def select[TOut](query:String,action:TupleQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.select[TOut](Data.Select(query,action,baseUrl))
  def select[TOut](data:Data.Select[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]
}

/*
trait that sends ask requests to reader actor
 */
trait Asker extends DataReader {
  /*
  Sends ask query to get True or False
   */
  //def question(query:String,action:AskQuering[Boolean], baseUrl:String = WI.RESOURCE): Future[Try[Boolean]] =  this.question[Boolean](Data.Ask(query,action,baseUrl))
  def question[TOut](query:String,action:AskQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] =  this.question[TOut](Data.Ask(query,action,baseUrl))
  def question[TOut](data:Data.Ask[TOut]): Future[Try[TOut]] = this.reader.ask(data)(readTimeout).mapTo[Try[TOut]]

}

trait Constructor extends DataReader {

  def construct[TOut](query:String,action:GraphQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.construct[TOut](Data.Construct(query,action,baseUrl))
  def construct[TOut](data:Data.Construct[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]

}

trait AnyQueryMaker extends DataReader {

  def anyQuery[TOut](query:String,action:AnyQuering[TOut], baseUrl:String = WI.RESOURCE): Future[Try[TOut]] = this.anyQuery[TOut](Data.AnyQuery[TOut](query,action,baseUrl))
  def anyQuery[TOut](data:Data.AnyQuery[TOut]): Future[Try[TOut]] = (this.reader ? data)(readTimeout).mapTo[Try[TOut]]


}