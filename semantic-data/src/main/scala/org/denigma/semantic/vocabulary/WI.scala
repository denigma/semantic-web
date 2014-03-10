package org.denigma.semantic.vocabulary

import org.openrdf.model.URI
import org.denigma.semantic.model.IRI
import org.denigma.semantic.sparql.Prefix

/*
refactor
 */
object WI extends PrefixConfig("http://webintelligence.eu/"){


  val RESOURCE: String = this / "resource"

  val CONFIG = this /+ "conf"

  val PAGES = this /+ "pages"

  val POLICY: PrefixConfig = this /+"policy"

  val SETTINGS: PrefixConfig =  this /+ "settings"

  val CLASSES: PrefixConfig = this /+ "classes"

  val PROPERTIES: PrefixConfig = this /+ "properties"

  def re(str:String): IRI = IRI(RESOURCE / str)

  def pg(page:String): IRI = IRI(PAGES / page)

  def conf(name:String): IRI = IRI(CONFIG / name)

  def po(str:String): IRI = IRI(POLICY /str)

  def set(res:URI): URI = { uris=uris+res; res}

  def set(name:String): URI= this set IRI(SETTINGS / name)

  var uris: Set[URI] = Set.empty[URI]

  val root: URI= this set "root"

  val context: URI = this set "context"
}



/**
 * Main namespace
 */
class PrefixConfig(val namespace:String)
{
  def / (name:String): String = namespace / name
  def /+(name:String) = new PrefixConfig(namespace / name)

  def stringValue = namespace
}
