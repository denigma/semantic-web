package org.denigma.rdf.model.vocabulary

import org.denigma.rdf.model.IRI


object USERS extends PrefixConfig(WI.namespace / "users")
{
  self=>

  val user: PrefixConfig = this /+ "user"


  object props {

    val hasPasswordHash = (WI.PROPERTIES / "hasPasswordHash").iri
    val hasEmail:IRI = FOAF.MBOX

  }

  object classes {
    val User: IRI = WI.CLASSES / "User" iri
  }




}
