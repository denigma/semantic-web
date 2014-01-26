/** *****************************************************************************
  * Copyright (c) 2009 TopQuadrant, Inc.
  * All rights reserved.
  * ******************************************************************************/
package org.denigma.semantic.classes

import java.util.List
import org.topbraid.spin.constraints.ConstraintViolation
import org.topbraid.spin.constraints.SPINConstraints
import org.topbraid.spin.inference.SPINInferences
import org.topbraid.spin.system.SPINLabels
import org.topbraid.spin.system.SPINModuleRegistry
import org.topbraid.spin.util.JenaUtil
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource

/**
 * Loads the Kennedys SPIN ontology and runs inferences and then
 * constraint checks on it.
 *
 * @author Holger Knublauch
 */
object KennedysInferencingAndConstraintsExample {
  def main(args: Array[String]) {
    SPINModuleRegistry.get.init
    val baseModel: Model = ModelFactory.createDefaultModel
    baseModel.read("http://topbraid.org/examples/kennedysSPIN")
    val ontModel: OntModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM, baseModel)
    val newTriples: Model = ModelFactory.createDefaultModel
    ontModel.addSubModel(newTriples)
    SPINModuleRegistry.get.registerAll(ontModel, null)
    SPINInferences.run(ontModel, newTriples, null, null, false, null)
    System.out.println("Inferred triples: " + newTriples.size)
    val cvs: List[ConstraintViolation] = SPINConstraints.check(ontModel, null)
    System.out.println("Constraint violations:")
    import scala.collection.JavaConversions._
    for (cv <- cvs) {
      System.out.println(" - at " + SPINLabels.get.getLabel(cv.getRoot) + ": " + cv.getMessage)
    }
    val person: Resource = cvs.get(0).getRoot
    val localCVS: List[ConstraintViolation] = SPINConstraints.check(person, null)
    System.out.println("Constraint violations for " + SPINLabels.get.getLabel(person) + ": " + localCVS.size)
  }
}


