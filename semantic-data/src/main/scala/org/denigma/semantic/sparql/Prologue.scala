package org.denigma.semantic.sparql

import org.denigma.semantic.model.IRI
import org.openrdf.model.Namespace


/**
 * Prefix class
 */
case class Prefix(prefix:String,iri:IRI) extends NameSpace(prefix,iri)
abstract class NameSpace(prefix:String,iri:IRI) extends Namespace
{
  override def compareTo(o: Namespace): Int =
    if (getPrefix == o.getPrefix)  if (getName == o.getName) 0 else getName.compareTo(o.getName)

    else getPrefix.compareTo(o.getPrefix)


  override def getPrefix: String = prefix

  override val getName: String = iri.stringValue


  override def toString = s"\nPREFIX $prefix: <$getName>"
}


case class Base(iri:IRI) extends NameSpace("",iri){
  override def toString = s"\nBASE <$getName>"

}