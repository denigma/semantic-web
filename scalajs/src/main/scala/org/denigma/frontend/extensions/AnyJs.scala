package org.denigma.frontend.extensions

import scala.scalajs.js.Undefined
import scala.scalajs.js
import org.scalajs.dom

/**
 * Is mixed in to be used in extensions
 */
trait AnyJs {
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

}
