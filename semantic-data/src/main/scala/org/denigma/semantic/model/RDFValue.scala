package org.denigma.semantic.model

import org.openrdf.model.Value

/**
RDF value implementation
 */
class RDFValue extends Value{
  override def stringValue: String = this.toString
}
