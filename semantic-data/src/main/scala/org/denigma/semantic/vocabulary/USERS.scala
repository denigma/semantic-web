package org.denigma.semantic.vocabulary

import com.bigdata.rdf.vocab.decls.FOAFVocabularyDecl
import org.denigma.semantic.sesame._
import org.denigma.rdf.IRI

object USERS extends PrefixConfig(WI.namespace / "users")
{
  self=>

  val user: PrefixConfig = this /+ "user"


  object props {

    val hasPasswordHash = (WI.PROPERTIES / "hasPasswordHash").iri
    val hasEmail:IRI = FOAFVocabularyDecl.mbox

  }

  object classes {
    val User: IRI = WI.CLASSES / "User" iri
  }




}
