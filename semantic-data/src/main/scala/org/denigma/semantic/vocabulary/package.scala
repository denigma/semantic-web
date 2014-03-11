package org.denigma.semantic

import org.openrdf.model.URI
import org.denigma.semantic.model.IRI

/**
 * implicit class that adds some imlicits
 */
package object vocabulary {


  implicit class URIPath(uri:URI) {
    def /(child:String): IRI = IRI(uri.stringValue / child)
    def /(child:URI): IRI = this / child.stringValue()

  }

  implicit class StringPath(str:String) {
    def /(child:String): String = if(str.endsWith("/")) str+child else str+"/"+child

    def /(child:URI): IRI =  IRI(str / child.toString)

    def iri: IRI = IRI(str)

  }
}
