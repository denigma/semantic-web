package org.denigma.semantic.classes

import org.topbraid.spin.arq.ARQ2SPIN
import org.topbraid.spin.arq.ARQFactory
import org.topbraid.spin.model.Query
import org.topbraid.spin.model.Select
import org.topbraid.spin.model.Template
import org.topbraid.spin.system.SPINModuleRegistry
import org.topbraid.spin.util.JenaUtil
import org.topbraid.spin.util.SystemTriples
import org.topbraid.spin.vocabulary.ARG
import org.topbraid.spin.vocabulary.SPIN
import org.topbraid.spin.vocabulary.SPL
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QuerySolutionMap
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.RDFNode
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.query


/**
 * Creates a SPIN template and "calls" it.
 *
 * @author Holger Knublauch
 */
object TemplateLearn {
  def main(args: Array[String]) {
    SPINModuleRegistry.get.init
    val model: Model = JenaUtil.createDefaultModel
    JenaUtil.initNamespaces(model.getGraph)
    model.add(SystemTriples.getVocabularyModel)
    model.setNsPrefix(PREFIX, NS)
    model.setNsPrefix(ARG.PREFIX, ARG.NS)
    val template: Template = createTemplate(model)
    val arq: query.Query = ARQFactory.get.createQuery(template.getBody.asInstanceOf[org.topbraid.spin.model.Select])
    val qexec: QueryExecution = ARQFactory.get.createQueryExecution(arq, model)
    val arqBindings: QuerySolutionMap = new QuerySolutionMap
    arqBindings.add("predicate", RDFS.label)
    qexec.setInitialBinding(arqBindings)
    val rs: ResultSet = qexec.execSelect
    val `object`: RDFNode = rs.next.get("object")
    System.out.println("Label is " + `object`)
  }

  def createTemplate(model: Model): Template = {
    val arqQuery: query.Query = ARQFactory.get.createQuery(model, QUERY)
    val spinQuery: Query = new ARQ2SPIN(model).createQuery(arqQuery, null)
    val template: Template = model.createResource(NS + "MyTemplate", SPIN.Template).as(classOf[Template])
    template.addProperty(SPIN.body, spinQuery)
    val argument: Resource = model.createResource(SPL.Argument)
    argument.addProperty(SPL.predicate, model.getProperty(ARG.NS + "predicate"))
    argument.addProperty(SPL.valueType, RDF.Property)
    argument.addProperty(RDFS.comment, "The predicate to get the value of.")
    template.addProperty(SPIN.constraint, argument)
    return template
  }

  val NS: String = "http://example.org/model#"
  val PREFIX: String = "ex"
  val QUERY: String = "SELECT *\n" + "WHERE {\n" + "    owl:Thing ?predicate ?object .\n" + "}"
}


