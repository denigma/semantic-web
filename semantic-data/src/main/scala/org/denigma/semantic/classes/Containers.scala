package org.denigma.semantic.classes

import org.openrdf.model.Resource
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.SemanticModel
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model

import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.denigma.semantic.classes.{SemanticResource, SemanticModel}
import org.denigma.semantic.data.{SemanticStore, QueryResult}
import org.denigma.semantic.SG
import org.openrdf.model.impl._
import org.openrdf.model._
import org.openrdf.model
import com.bigdata.rdf.sail.BigdataSailRepositoryConnection
import org.openrdf.model.{vocabulary, Literal, Resource, Statement}
import org.openrdf.model.vocabulary._
import javax.xml.datatype.XMLGregorianCalendar


object OwlRestriction extends OfSemanticType(OWL.RESTRICTION)


object RdfProperty extends OfSemanticType(RDF.PROPERTY,OWL.DATATYPEPROPERTY,OWL.INVERSEFUNCTIONALPROPERTY,OWL.TRANSITIVEPROPERTY,OWL.OBJECTPROPERTY)

object RdfAlt extends OfSemanticType(RDF.ALT)
object RdfBag extends OfSemanticType(RDF.BAG)
object RdfSeq extends OfSemanticType(RDF.SEQ)
object RdfList extends OfSemanticType(RDF.LIST)

