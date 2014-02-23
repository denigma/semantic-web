package org.denigma.semantic.controllers

import org.openrdf.query.{QueryLanguage, TupleQueryResult}
import org.denigma.semantic.actors._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.ask
import org.denigma.semantic.data._
import scala.reflect.ClassTag
import org.denigma.semantic.WI
import scala.util.Try
import scala.concurrent.duration._
import scala.concurrent.duration.Duration
import org.denigma.semantic.quering.{QueryResult, DefaultQueryModifier, QueryModifier}
import com.bigdata.rdf.sparql.ast.SliceNode

/*
query controller with some additional features
 */
trait QueryController extends SemanticController{


}
