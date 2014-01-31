package org.denigma.semantic.data

import com.bigdata.rdf.sail._
import scala.util.{Failure, Success, Try}
import org.openrdf.query.{GraphQueryResult, TupleQueryResult, TupleQuery, QueryLanguage}
import com.hp.hpl.jena.sparql.syntax._
import com.hp.hpl.jena.sparql._
import com.hp.hpl.jena.query._
import scala.util.Success
import scala.util.Failure
import org.openrdf.query.algebra._
import com.hp.hpl.jena.rdf.model.{Resource, ModelFactory}
import scala.util.Success
import scala.util.Failure
import org.openrdf.query.parser.{ParsedTupleQuery, ParsedUpdate}
import scala.collection.JavaConversions._
import com.bigdata.rdf.sparql.ast.optimizers.IASTOptimizer
import com.bigdata.rdf.sparql.ast.{SliceNode, IQueryNode, ASTContainer}
import com.bigdata.rdf.sail.sparql.ast.{ASTLimit, ASTQuery, ASTQueryContainer}
import com.bigdata.bop.{BOp, IBindingSet}
import com.bigdata.rdf.sparql.ast.eval.AST2BOpContext
import com.bigdata.rdf.sail.sparql.BigdataASTContext
import java.net.URL
import org.openrdf.rio.Rio
import com.bigdata.rdf.model.BigdataURI

/**
 * Created by antonkulaga on 1/20/14.
 */
abstract class RDFStore {

  val lg:org.slf4j.Logger

  val repo: BigdataSailRepository

  def read[T](action:BigdataSailRepositoryConnection=>T):Try[T]= {
    val con = repo.getReadOnlyConnection
    val res = Try {
      action(con)
    }

    con.close()
    res.recoverWith{case
      e=>
      lg.error("readonly transaction from database failed because of \n"+e.getMessage)
      res
    }
  }

  /*
  writes something and then closes the connection
   */
  def readWrite[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = repo.getConnection
    con.setAutoCommit(false)
    val res = Try {
      val r = action(con)
      con.commit()
      r
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error("read/write transaction from database failed because of \n"+e.getMessage)
      res
    }
  }

  /*
parses RDF file
 */
  def parseFile(path:String,contextStr:String="") = {
    val url = new URL(path)
    val inputStream = url.openStream()
    val format = Rio.getParserFormatForFileName(url.toString)
    val parser = Rio.createParser(format)
    this.write{con=>
      val context: BigdataURI = if(contextStr=="") null else con.getValueFactory.createURI(contextStr)
      val r = new RdfReader(path,con,context)(lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, url.toString)
    }


  }

  /*
 Shutdown repository
  */
  def close()={
    repo.shutDown()
  }

  /*
does something with Sesame connection and then closes it
 */
  def withConnection(con:BigdataSailRepositoryConnection)(action:BigdataSailRepositoryConnection=>Unit) = {
    if(!con.isReadOnly) con.setAutoCommit(false)
    Try {
      action(con)
      if(!con.isReadOnly) con.commit()
    }.recover{case f=>lg.error(f.toString)}
    lg.debug("operation successful")
    con.close()
  }


  /*
  writes something and then closes the connection
   */
  def write(action:BigdataSailRepositoryConnection=>Unit):Boolean = this.readWrite[Unit](action) match
  {
    case Success(_)=>true
    case Failure(e)=>false
  }





}

