package org.denigma.semantic.schema

import org.denigma.semantic.controllers.WithLogger
import org.openrdf.model.vocabulary.{OWL, RDF, RDFS}
import org.denigma.semantic.actors.WatchProtocol.PatternResult
import org.denigma.semantic.actors.cache.PatternCache
import org.denigma.semantic.actors.cache.Cache.UpdateInfo
import org.scalax.semweb.sesame._
import org.scalax.semweb.sparql._


/**
 * Activates
 */
object Schema extends PatternCache with WithLogger
{


  override val cacheName: String = "Schema"

  override def updateHandler(updateInfo: UpdateInfo): Unit = {

  }

  override def onResult(p: PatternResult): Unit = {
    //lg.debug(s"GRAPH cache received ${p.toString}")
  }
  val classes:Set[Pat] = Set(
    Pat(?("class"),URI2IRI(RDFS.SUBCLASSOF),?("parent")),
    Pat(?("class"),URI2IRI(RDF.TYPE),URI2IRI(RDFS.CLASS)),
    Pat(?("class"),URI2IRI(RDF.TYPE),URI2IRI(OWL.CLASS)),
    Pat(?("class"),URI2IRI(OWL.EQUIVALENTCLASS),?("eqv"))

  )

  val properties:Set[Pat] = Set(
    Pat(?("prop"),URI2IRI(RDFS.DOMAIN),?("domain")),
    Pat(?("prop"),URI2IRI(RDFS.RANGE),?("range")),
    Pat(?("prop"),URI2IRI(RDF.TYPE),URI2IRI(RDF.PROPERTY)),
    Pat(?("prop"),URI2IRI(RDF.TYPE),URI2IRI(OWL.OBJECTPROPERTY)),
    Pat(?("prop"),URI2IRI(RDF.TYPE),URI2IRI(OWL.DATATYPEPROPERTY)),
    Pat(?("prop"),URI2IRI(RDF.TYPE),URI2IRI(OWL.FUNCTIONALPROPERTY)),
    Pat(?("prop"),URI2IRI(OWL.INVERSEFUNCTIONALPROPERTY), ?("other")),
    Pat(?("prop"),URI2IRI(RDF.TYPE),URI2IRI(OWL.INVERSEFUNCTIONALPROPERTY))
    //TODO: add others
  )
  override var patterns: Set[Pat] = this.classes++this.properties


}
