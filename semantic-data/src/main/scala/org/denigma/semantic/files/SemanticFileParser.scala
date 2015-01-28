package org.denigma.semantic.files

import java.net.URL
import java.io.{FileInputStream, InputStream, File}
import com.bigdata.rdf.rio.turtle.BigdataTurtleParser
import org.openrdf.rio.Rio
import com.bigdata.rdf.model.BigdataURI
import org.denigma.semantic.writing.DataWriter
import org.openrdf.rio.helpers.{TurtleParserSettings, BasicParserSettings}
import scala.util.Try
import com.bigdata.rdf.rio.turtle.BigdataTurtleParserFactory
/*
trait that parses files and stores them in semanticweb store
 */
trait SemanticFileParser extends DataWriter
{

  /*
  parses file by its path
   */
  def parseFileByName(path:String,contextStr:String=""): Try[Unit] = {
    val url = if(path.contains(":")) new URL(path) else new File(path).toURI.toURL
    //this.parseStream(url.toString,path,url.openStream(),contextStr)
    this.parseTurtle(url.toString,path,url.openStream(),contextStr)

  }

  /*
  parses file
   */
  def parseFile(file:File,contextStr:String=""): Try[Unit] = {
    if(!file.exists()) file.createNewFile()
    val stream: FileInputStream = new FileInputStream(file)
//    this.parseStream(file.getName,file.getAbsolutePath,stream,contextStr)
    this.parseTurtle(file.getName,file.getAbsolutePath,stream,contextStr) //TEMPORALY

  }

  def parseTurtle(fileName:String,path:String,inputStream:InputStream,contextStr:String=""): Try[Unit] = {
    this.write{con=>
      val parser = new BigdataTurtleParser()
      parser.setStopAtFirstError(false)
      con.setAutoCommit(false)
      val context: BigdataURI = if (contextStr == "") null else con.getValueFactory.createURI(contextStr)
      val r = new SemanticFileListener(path, con, context)(lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, fileName)
    }
  }

  /*
  parses input stream of data
   */
/*  def parseStream(fileName:String,path:String,inputStream:InputStream,contextStr:String=""): Try[Unit] = {
    val format = Rio.getParserFormatForFileName(fileName)
    val parser = Rio.createParser(format)
    parser.getParserConfig.set(BasicParserSettings.DATATYPE_HANDLERS, false)
    parser.getParserConfig.set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES,false)
    parser.getParserConfig.set(BasicParserSettings.PRESERVE_BNODE_IDS,true)
    parser.setStopAtFirstError(false)
    val con = this.writeConnection
    con.setAutoCommit(false)
    val context: BigdataURI = if (contextStr == "") null else con.getValueFactory.createURI(contextStr)
    val r = new SemanticFileListener(path, con, context)(lg)
    parser.setRDFHandler(r)
    parser.setParseErrorListener(r)
    parser.parse(inputStream, fileName)
    r.result
  }*/
}
