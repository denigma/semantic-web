package org.denigma.semantic.files

import com.bigdata.rdf.sail._
import org.{openrdf=>se}
import org.openrdf.model._
import com.bigdata.rdf.model.BigdataStatement
import se.rio._
import org.denigma.semantic.commons.LogLike


/*
class that reads RDF
 */
class SemanticFileListener(fileName:String,con:BigdataSailRepositoryConnection, context: se.model.Resource = null)(implicit lg:LogLike) extends RDFHandler with ParseErrorListener{

  lazy val f = con.getValueFactory

  override def handleComment(comment: String): Unit = {
    //comment

  }

  /*
  adds parsed statements
   */
  override def handleStatement(st: Statement): Unit =  if(context==null){
    con.add(st)

  }  else {
    val stq: BigdataStatement = f.createStatement(st.getSubject,st.getPredicate,st.getObject,context)
    con add stq
  }

  override def handleNamespace(prefix: String, uri: String): Unit = if(prefix!=null && con.getNamespace(prefix)==null) {
      con.setNamespace(prefix,uri)
      //lg.debug(s"set prefix $prefix for namespace $uri")
  }

  override def endRDF(): Unit = {
    con.commit()
  }

  override def startRDF(): Unit = {
    lg.info(s"$fileName parsing has started")

  }

  override def fatalError(msg: String, lineNo: Int, colNo: Int): Unit = {
    lg.error(s"FATAL error $msg at LINE $lineNo COL $colNo occurred when parsing:  $fileName")
    con.rollback()
  }

  override def error(msg: String, lineNo: Int, colNo: Int): Unit = {
    lg.error(s"nonfatal error $msg at LINE $lineNo COL $colNo occurred when parsing:  $fileName")
  }

  override def warning(msg: String, lineNo: Int, colNo: Int): Unit = {
    lg.warn(s"WARNING $msg at LINE $lineNo COL $colNo occurred when parsing:  $fileName")
  }
}

