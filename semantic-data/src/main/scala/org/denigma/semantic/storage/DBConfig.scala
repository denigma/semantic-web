package org.denigma.semantic.storage

import play.api.Configuration
import java.util.Properties

/**
 * Created by antonkulaga on 2/24/14.
 */
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
