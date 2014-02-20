package org.denigma.semantic

import java.util.Properties
import java.io._
import org.denigma.semantic.data.SemanticStore
import java.util
import org.openrdf.repository.RepositoryResult

//import org.apache.log4j.Logger
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Play}
import play.api.Play.current
import org.openrdf.model._
import scala.collection.immutable._
import scala.collection.JavaConversions._

/*
configuration of the database
 */
class DBConfig(val dbConf:Configuration) {

  val truthMaintenance: Boolean = dbConf.getBoolean("com.bigdata.rdf.sail.truthMaintenance").getOrElse(false)
  val storeConf = dbConf.getConfig("com.bigdata.rdf.store.AbstractTripleStore").get
  val quads = storeConf.getBoolean("quadsMode").getOrElse(true)
  val stsIds = storeConf.getBoolean("statementIdentifiers").getOrElse(false)
  val textIndex = storeConf.getBoolean("textIndex").getOrElse(true)
  val limit: Long = this.dbConf.getLong("limit").getOrElse(50)
  val url:String = dbConf.getString("url").get
  val dbFileName: String = dbConf.getString("name").getOrElse("bigdata.jnl")



  /*
  BigData settings
  TODO: load from play config
   */
  lazy val properties:Properties = {
    val props = new Properties()
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.quadsMode",quads.toString)
    props.setProperty("com.bigdata.rdf.store.AbstractTripleStore.textIndex",textIndex.toString)
    props.setProperty("com.bigdata.rdf.sail.truthMaintenance",truthMaintenance.toString)
    props.setProperty("com.bigdata.rdf.sail.statementIdentifiers",stsIds.toString)
    props.setProperty("com.bigdata.journal.AbstractJournal.file",url+"/"+dbFileName)

    //props.setProperty("com.bigdata.journal.AbstractJournal.initialExtent","209715200")
    //props.setProperty("com.bigdata.journal.AbstractJournal.maximumExtent","209715200")
    props
  }


}

/*
Config of SemanticPlatform object
 */
class PlatformConfig(conf:Configuration){

  val filesConf: scala.List[Configuration] = conf.getConfigList("files").map(_.toList).getOrElse(Nil)
  //lazy val semanticConf = conf.getConfig("semantic")

  val CONFIG_CONTEXT = conf.getString("config_context").getOrElse(WI.CONFIG)
  val MAIN_CONTEXT =  conf.getString("main_context").getOrElse(WI.RESOURCE)
  val POLICY_CONTEXT =  conf.getString("main_context").getOrElse(WI.POLICY)
  val loadInitial = conf.getBoolean("load_initial").getOrElse(true)

  val readers = conf.getConfig("readers").get
  val minReaders = readers.getInt("min").getOrElse(3)
  val defReaders = readers.getInt("def").getOrElse(3)
  val maxReaders = readers.getInt("max").getOrElse(3)



}