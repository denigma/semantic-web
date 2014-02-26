package org.denigma.semantic.classes

import org.openrdf.model.vocabulary._


object OwlRestriction extends OfSemanticType(OWL.RESTRICTION)


object RdfProperty extends OfSemanticType(RDF.PROPERTY,OWL.DATATYPEPROPERTY,OWL.INVERSEFUNCTIONALPROPERTY,OWL.TRANSITIVEPROPERTY,OWL.OBJECTPROPERTY)

object RdfAlt extends OfSemanticType(RDF.ALT)
object RdfBag extends OfSemanticType(RDF.BAG)
object RdfSeq extends OfSemanticType(RDF.SEQ)
object RdfList extends OfSemanticType(RDF.LIST)

