package org.denigma.extensions

import org.scalajs.dom.{Attr, NamedNodeMap}
import scala.collection.mutable
import org.scalajs.dom

/**
 * Attribues
 */
trait AttributesOps {

  /**
   * Creates and attribute
   * @param tuple
   */
  implicit class AttrFactory(tuple:(String,String))  {
    
    def toAtt: Attr = {
      val at = dom.document.createAttribute(tuple._1)
      at.value = tuple._2
      at
    }
  }


  implicit class Attributes(attributes:NamedNodeMap) extends mutable.Map[String,Attr] {
    self =>

    override def iterator: Iterator[(String, Attr)] = new Iterator[(String, Attr)] {
      var index = 0

      override def next(): (String, Attr) = {
        val n: Attr = attributes.item(index)
        this.index = this.index + 1
        (n.name, n)
      }

      override def hasNext: Boolean = index < self.length
    }

    override def get(key: String): Option[Attr] = attributes.getNamedItem(key) match {
      case null => None
      case attr => Some(attr)
    }

    def length: Int = attributes.length.toInt

    override def -=(key: String) = {
      attributes.removeNamedItem(key)
      this
    }

    override def empty: Attributes = Attributes.empty //This class should not be initiated
    override def +=(kv: (String, Attr)) = {
      attributes.setNamedItem(kv._2)
      this}
  }

  object Attributes {

    /**
     * Not sure what to do with it
     */
    val empty:Attributes = new Attributes(null)
  }


}
