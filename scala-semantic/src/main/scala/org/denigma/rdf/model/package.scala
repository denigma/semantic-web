package org.denigma.rdf


package object model {

  /**
   * TEMPORAL FIX
   * @param value
   */
  implicit class LabelHolder(value:RDFValue) {

    def label = value match {
      case l:Lit=>l.label
      case other=>value.stringValue
    }
  }
}
