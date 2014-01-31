package org.denigma.semantic.data

import com.bigdata.rdf.sail._
import scala.util.{Failure, Try}
import org.openrdf.query.{TupleQueryResult, TupleQuery, QueryLanguage}
import com.hp.hpl.jena.sparql._
import com.hp.hpl.jena.query._
import scala.util.Success
import com.hp.hpl.jena.rdf.model.ModelFactory
import scala.util.Success
import scala.util.Failure
import org.openrdf.query.parser.ParsedUpdate
import com.bigdata.rdf.sparql.ast.{SliceNode, ASTContainer}
import com.bigdata.rdf.sail.sparql.ast.{ASTLimit, ASTQuery}
import com.bigdata.bop.BOp
import com.bigdata.rdf.sail.sparql.BigdataASTContext
import java.io.File
import java.util.Properties
import org.openrdf.rio.{RDFHandler, Rio}
import java.net.URL
import org.{openrdf=>se}
import org.openrdf.model._
import org.openrdf.model.impl._
import com.bigdata.rdf.model.BigdataURI
import se.rio._


/**
 * Created by antonkulaga on 1/20/14.
 */
abstract class SemanticStore extends SemanticQueries{


  val dbFileName:String

  val properties:Properties

  val sail: BigdataSail = {
    //val log =  Logger.getLogger(classOf[BigdataSail])

    // create a backing file for the database
    //val journal = File.createTempFile("bigdata", ".jnl")

    if (properties.getProperty(com.bigdata.journal.Options.FILE) == null) {
      val journal = new File(this.dbFileName)
      if(!journal.exists())journal.createNewFile()

      //val oFile = new FileOutputStream(journal, false)

      //log.info(journal.getAbsolutePath)
      properties.setProperty(BigdataSail.Options.DEFAULT_FILE, journal.getAbsolutePath)
    }


    new BigdataSail(properties)
  }

  /*
  Bigdata Sesame repository
   */
  lazy val repo: BigdataSailRepository = {
    val repo = new BigdataSailRepository(sail)
    repo.initialize()
    repo
  }


}