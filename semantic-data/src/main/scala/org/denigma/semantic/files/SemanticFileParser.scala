package org.denigma.semantic.files

import java.net.URL
import java.io.File
import org.openrdf.rio.Rio
import com.bigdata.rdf.model.BigdataURI
import org.denigma.semantic.writing.UpdateWriter

/**
 * Created by antonkulaga on 2/24/14.
 */
trait SemanticFileParser extends UpdateWriter{
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
      val r = new SemanticFileListener(path,con,context)(lg)
      parser.setRDFHandler(r)
      parser.setParseErrorListener(r)
      parser.parse(inputStream, url.toString)
    }


  }
}
