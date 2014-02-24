package org.denigma.semantic.data

import com.bigdata.rdf.sail._
import scala.util.Try

import scala.util.Success
import scala.util.Failure
import java.net.URL
import org.openrdf.rio.Rio
import com.bigdata.rdf.model.BigdataURI
import java.io.File
import org.openrdf.query.QueryLanguage
import org.denigma.semantic.WI


/*class that deals with storing and retrieving RDF from bigdata storage*/
abstract class RDFStore {



  val lg:org.slf4j.Logger

  val repo: BigdataSailRepository

  def read[T](action:Reading[T]):Try[T]= {
    val con: BigdataSailRepositoryConnection = repo.getReadOnlyConnection
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
  readonly select query failed
   */
  def selectQuery[T](str:String,select:TupleQuering[T])(implicit base:String = WI.RESOURCE) = {
    val con: BigdataSailRepositoryConnection = repo.getReadOnlyConnection
    val q:BigdataSailTupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,str,base)
    val res = Try{
      select(str,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly SELECT query\n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }

  //def askQuery(str:String,ask:AskQuering[Boolean])(implicit base:String = WI.RESOURCE): Try[Boolean] =    askQuery[Boolean](str,ask)(base)

  /*
 readonly select query failed
  */
  def askQuery[T](str:String,ask:AskQuering[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con: BigdataSailRepositoryConnection = repo.getReadOnlyConnection
    val q = con.prepareBooleanQuery(QueryLanguage.SPARQL,str,base)
    val res = Try{
      ask(str,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly ASK query\n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }

  def graphQuery[T](str:String,selectGraph:GraphQuering[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con: BigdataSailRepositoryConnection = repo.getReadOnlyConnection
    val q: BigdataSailGraphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL,str,base)
    val res = Try{
      selectGraph(str,con,q)

    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"readonly GRAPH query\n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }

  def anyQuery[T](str:String,select:AnyQuering[T])(implicit base:String = WI.RESOURCE): Try[T] = {
    val con: BigdataSailRepositoryConnection = repo.getReadOnlyConnection
    val q: BigdataSailQuery = con.prepareNativeSPARQLQuery(QueryLanguage.SPARQL,str,base)
    val res = Try{
      select(str,con,q)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"READONLY any QUERY \n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }

  //def update(str:String,update:UpdateQuering[Unit])(implicit base:String = WI.RESOURCE): Try[Unit] = this.update[Unit](str,update)(base)


  def update[T](str:String,update:UpdateQuering[T])(implicit base:String = WI.RESOURCE) = {
    val con: BigdataSailRepositoryConnection = repo.getUnisolatedConnection
    con.setAutoCommit(false)
    val u = con.prepareNativeSPARQLUpdate(QueryLanguage.SPARQL,str,base)
    val res = Try{
      update(str,con,u)
    }
    con.close()
    res.recoverWith{case
      e=>
      lg.error(s"UPDATE query \n $str \nfailed because of \n"+e.getMessage)
      res
    }

  }

  /*
  writes something and then closes the connection
   */
  def readWrite[T](action:BigdataSailRepositoryConnection=>T):Try[T] =
  {
    val con = repo.getUnisolatedConnection
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
  def parseFile(path:String,contextStr:String=""): Boolean = {


    val url = if(path.contains(":")) new URL(path) else new File(path).toURI.toURL
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

