package org.denigma.semantic.files

import java.net.URL
import java.io.{FileInputStream, InputStream, File}
import org.openrdf.rio.Rio
import com.bigdata.rdf.model.BigdataURI
import org.denigma.semantic.writing.DataWriter
import scala.util.Try

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
    this.parseStream(url.toString,path,url.openStream(),contextStr)
  }

  /*
  parses file
   */
  def parseFile(file:File,contextStr:String=""): Try[Unit] = {
    if(!file.exists()) file.createNewFile()
    val stream: FileInputStream = new FileInputStream(file)
    this.parseStream(file.getName,file.getAbsolutePath,stream,contextStr)
  }

  /*
  parses input stream of data
   */
  def parseStream(fileName:String,path:String,inputStream:InputStream,contextStr:String=""): Try[Unit] = {


    val format = Rio.getParserFormatForFileName(fileName)
    val parser = Rio.createParser(format)
    this.write{con=>
      val context: BigdataURI = if(contextStr=="") null else con.getValueFactory.createURI(contextStr)
      val r = new SemanticFileListener(path,con,context)(lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, fileName)
    }

  }
}
