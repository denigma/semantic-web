package org.denigma.frontend

import org.scalajs.dom._

import org.scalajs.dom.{Attr, NamedNodeMap}
import scala.collection.mutable
import scala.scalajs.js.Undefined
import scala.scalajs.js
import org.scalajs.dom
import org.denigma.macroses.js.ClassToMap

/**
 * Useful implicit classes
 */
package object extensions {


  /**
   * Implicit class that adds some useful methods for any ScalaJS object
   * @param obj
   */
  implicit class AnyJs(obj:scalajs.js.Any){
    /**
     * As Javascript has both null and undefined often if(element.obj==null) returns false when obj is undefined,
     * it is especially  unpleasant with dom wrappers
     * @param other another object to compare to
     * @return
     */
    def ===(other:Any): Boolean = if(other==null) obj==null || obj.isInstanceOf[Undefined] || obj=="" else obj==other

    /**
     * Just a shorter conversion to dynamic object
     * @return self as Dynamic
     */
    def dyn = obj.asInstanceOf[js.Dynamic]

    /**
     * provides dynamic results as options
     * @param key name of the property
     * @return Option[js.Dynamic]
     */
    def \ (key:String): Option[js.Dynamic] = dyn.selectDynamic(key) match {
      case null=>
        dom.document.getElementById("").parentElement.tagName
        None
      case v:Undefined=>None
      case validValue=>Some(validValue)
    }
  }

  /**
   * Useful for complicated traversals, like
   *  grandfather \ "mother" \ "daughter"
   * @param opt option with Dynamic object
   */
  implicit class OptionPath(opt:Option[js.Dynamic]){
    def \ (key:String): Option[js.Dynamic] = opt.flatMap(_ \ key)
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

