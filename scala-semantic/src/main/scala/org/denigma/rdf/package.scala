package org.denigma

/**
 * Created by antonkulaga on 18.04.14.
 */
package object rdf {

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
