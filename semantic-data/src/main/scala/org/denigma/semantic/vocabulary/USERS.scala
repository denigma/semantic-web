package org.denigma.semantic.vocabulary

import org.denigma.semantic.model.IRI
import org.openrdf.model.URI
import org.denigma.semantic.vocabulary._
import org.openrdf.model.{vocabulary=>voc}
import org.openrdf.model.vocabulary.FOAF

object USERS extends PrefixConfig(WI.namespace / "users")
{
  self=>

  val user: PrefixConfig = this /+ "user"


  object props {

    val hasPasswordHash = (WI.PROPERTIES / "hasPasswordHash").iri
    val hasEmail = FOAF.MBOX

  }




}
