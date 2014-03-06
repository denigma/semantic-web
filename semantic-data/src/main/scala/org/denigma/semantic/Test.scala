package org.denigma.semantic

import org.denigma.semantic.sparql._
import org.denigma.semantic.sparql.Pat
import org.denigma.semantic.model.IRI
import org.openrdf.model.impl.LiteralImpl

object Test {
  //  import org.denigma.semantic.sparql._
  //  import org.denigma.semantic.model._

  InsertQuery {
    INSERT apply  DATA { Trip(IRI("http://some"),IRI("http://some"), new LiteralImpl("some")) }
  }

}