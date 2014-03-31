package org.denigma.extensions

import org.scalajs.dom.HTMLElement
import scala.scalajs.js

/**
 * Created by antonkulaga on 31.03.14.
 */
class ElementOps {

  implicit class Element(el:HTMLElement) {


    def updateIfExist(key:String,value:js.Any) = if(el.hasOwnProperty(key) && el.dyn.selectDynamic(key)!=value)
      el.dyn.updateDynamic(key)(value)


  }

}
