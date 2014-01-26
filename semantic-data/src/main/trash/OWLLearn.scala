/** *****************************************************************************
  * Copyright (c) 2009 TopQuadrant, Inc.
  * All rights reserved.
  * ******************************************************************************/
package org.denigma.semantic.classes

import java.util.List
import java.util.Map
import org.topbraid.spin.inference.DefaultSPINRuleComparator
import org.topbraid.spin.inference.SPINInferences
import org.topbraid.spin.inference.SPINRuleComparator
import org.topbraid.spin.system.SPINModuleRegistry
import org.topbraid.spin.util.CommandWrapper
import org.topbraid.spin.util.JenaUtil
import org.topbraid.spin.util.SPINQueryFinder
import org.topbraid.spin.vocabulary.SPIN
import com.hp.hpl.jena.graph.Graph
import com.hp.hpl.jena.graph.compose.MultiUnion
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource

/**
 * Demonstrates how to efficiently use an external SPIN library, such as OWL RL
 * to run inferences on a given Jena model.
 *
 * The main trick is that the Query maps are constructed beforehand, so that the
 * actual query model does not need to include the OWL RL model at execution time.
 *
 * @author Holger Knublauch
 */
object OWLRLExample {
  def main(args: Array[String]) {
    SPINModuleRegistry.get.init
    System.out.println("Loading domain ontology...")
    val queryModel: OntModel = loadModelWithImports("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl")
    val newTriples: Model = ModelFactory.createDefaultModel
    queryModel.addSubModel(newTriples)
    System.out.println("Loading OWL RL ontology...")
    val owlrlModel: OntModel = loadModelWithImports("http://topbraid.org/spin/owlrl-all")
    SPINModuleRegistry.get.registerAll(owlrlModel, null)
    val multiUnion: MultiUnion = JenaUtil.createMultiUnion(Array[Graph](queryModel.getGraph, owlrlModel.getGraph))
    val unionModel: Model = ModelFactory.createModelForGraph(multiUnion)
    val cls2Query: Map[Resource, List[CommandWrapper]] = SPINQueryFinder.getClass2QueryMap(unionModel, queryModel, SPIN.rule, true, false)
    val cls2Constructor: Map[Resource, List[CommandWrapper]] = SPINQueryFinder.getClass2QueryMap(queryModel, queryModel, SPIN.constructor, true, false)
    val comparator: SPINRuleComparator = new DefaultSPINRuleComparator(queryModel)
    System.out.println("Running SPIN inferences...")
    SPINInferences.run(queryModel, newTriples, cls2Query, cls2Constructor, null, null, false, SPIN.rule, comparator, null)
    System.out.println("Inferred triples: " + newTriples.size)
  }

  private def loadModelWithImports(url: String): OntModel = {
    val baseModel: Model = ModelFactory.createDefaultModel
    baseModel.read(url)
    JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM, baseModel)
  }
}


