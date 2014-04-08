package org.denigma


import scala.scalajs.js.Dynamic.{global => g}

/**
 * Useful implicit classes
 */
package object extensions extends AttributesOps with AnyJs with RxOps with CommonOps{


  implicit class StringOps(str:String) {

    def localName: String = if(str.contains("/")) str.substring(str.lastIndexOf("/")) else str


  }

}