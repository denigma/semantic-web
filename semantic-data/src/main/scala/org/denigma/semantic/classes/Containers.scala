package org.denigma.semantic.classes

import org.openrdf.model.Resource
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.SemanticPlatform
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.SemanticModel
import org.denigma.semantic.SemanticPlatform
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.{SemanticResource, SemanticModel}
import org.denigma.semantic.SemanticPlatform
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import org.openrdf.model.vocabulary._
import javax.xml.datatype.XMLGregorianCalendar
import org.denigma.semantic.storage.SemanticStore


object OwlRestriction extends OfSemanticType(OWL.RESTRICTION)


object RdfProperty extends OfSemanticType(RDF.PROPERTY,OWL.DATATYPEPROPERTY,OWL.INVERSEFUNCTIONALPROPERTY,OWL.TRANSITIVEPROPERTY,OWL.OBJECTPROPERTY)

object RdfAlt extends OfSemanticType(RDF.ALT)
object RdfBag extends OfSemanticType(RDF.BAG)
object RdfSeq extends OfSemanticType(RDF.SEQ)
object RdfList extends OfSemanticType(RDF.LIST)

