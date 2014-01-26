/** *****************************************************************************
  * Copyright (c) 2009 TopQuadrant, Inc.
  * All rights reserved.
  * ******************************************************************************/
package org.denigma.semantic.classes

import org.topbraid.spin.arq.ARQ2SPIN
import org.topbraid.spin.arq.ARQFactory
import org.topbraid.spin.model.Select
import org.topbraid.spin.system.SPINModuleRegistry
import com.hp.hpl.jena.query.{Syntax, Query}
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.util.FileUtils
import com.hp.hpl.jena.vocabulary.RDF


/**
 * Converts between textual SPARQL representation and SPIN RDF model.
 *
 * @author Holger Knublauch
 */
object SPINParsingExample {
  def main(args: Array[String]) {
    SPINModuleRegistry.get.init
    val model: Model = ModelFactory.createDefaultModel
    model.setNsPrefix("rdf", RDF.getURI)
    model.setNsPrefix("ex", "http://example.org/demo#")
    val query: String = "SELECT ?person\n" + "WHERE {\n" + "    ?person a ex:Person .\n" + "    ?person ex:age ?age .\n" + "    FILTER (?age > 18) .\n" + "}"
    val arqQuery: Query = ARQFactory.get.createQuery(model, query)
    arqQuery.toString(Syntax.syntaxSPARQL_11)
    val arq2SPIN: ARQ2SPIN = new ARQ2SPIN(model)
    val spinQuery: Select = arq2SPIN.createQuery(arqQuery, null).asInstanceOf[Select]
    System.out.println("SPIN query in Turtle:")
    model.write(System.out, FileUtils.langTurtle)
    System.out.println("-----")
    val str: String = spinQuery.toString
    System.out.println("SPIN query:\n" + str)
    val parsedBack: Query = ARQFactory.get.createQuery(spinQuery)
    System.out.println("Jena query:\n" + parsedBack)
  }
}


