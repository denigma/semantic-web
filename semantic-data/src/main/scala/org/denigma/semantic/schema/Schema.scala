package org.denigma.semantic.schema

import org.denigma.semantic.controllers.WithLogger
import org.openrdf.model.vocabulary.{OWL, RDF, RDFS}
import org.denigma.semantic.sparql._
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.actors.cache.{PatternCache}
import org.denigma.semantic.actors.cache.Cache.UpdateInfo

/**
 * Activates
 */
object Schema extends PatternCache with WithLogger
{

  override val name: String = "Schema"

  override def updateHandler(updateInfo: UpdateInfo): Unit = {

  }

  override def onResult(p: PatternResult): Unit = {
    lg.debug(s"GRAPH cache received ${p.toString}")
  }
  val classes:Set[Pat] = Set(
    Pat(?("class"),RDFS.SUBCLASSOF,?("parent")),
    Pat(?("class"),RDF.TYPE,RDFS.CLASS),
    Pat(?("class"),RDF.TYPE,OWL.CLASS),
    Pat(?("class"),OWL.EQUIVALENTCLASS,?("eqv"))

  )

  val properties:Set[Pat] = Set(
    Pat(?("prop"),RDFS.DOMAIN,?("domain")),
    Pat(?("prop"),RDFS.RANGE,?("range")),
    Pat(?("prop"),RDF.TYPE,RDF.PROPERTY),
    Pat(?("prop"),RDF.TYPE,OWL.OBJECTPROPERTY),
    Pat(?("prop"),RDF.TYPE,OWL.DATATYPEPROPERTY),
    Pat(?("prop"),RDF.TYPE,OWL.FUNCTIONALPROPERTY),
    Pat(?("prop"),OWL.INVERSEFUNCTIONALPROPERTY, ?("other")),
    Pat(?("prop"),RDF.TYPE,OWL.INVERSEFUNCTIONALPROPERTY)
    //TODO: add others
  )
  override var patterns: Set[Pat] = this.classes++this.properties


}
