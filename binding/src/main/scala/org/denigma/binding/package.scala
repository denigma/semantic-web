package org.denigma

import org.denigma.binding.macroses._
import rx._
import scalatags.HtmlTag
import scala.collection.immutable.Map
import org.scalajs.dom.{KeyboardEvent, MouseEvent, Event}

/**
 * Binding implicits
 */
package object binding {



    //    def extractTags:Map[String,Rx[HtmlTag]] = macro Binder.extractByType_impl[T ,Rx[HtmlTag]]
//    def extractStrings:Map[String,Rx[String]] = macro Binder.extractByType_impl[T ,Rx[String]]
//    def extractDoubles:Map[String,Rx[Double]] = macro Binder.extractByType_impl[T ,Rx[Double]]
//    def extractLists:Map[String,Rx[List[Map[String,Any]]]] = macro Binder.extractByType_impl[T ,Rx[List[Map[String,Any]]]]
//    def extractBools:Map[String,Rx[Boolean]] = macro Binder.extractByType_impl[T ,Rx[Boolean]]
//    def extractEvents:Map[String,Rx[Event]] = macro Binder.extractByType_impl[T ,Rx[Event]]
//    def extractMouseEvents:Map[String,Rx[MouseEvent]] = macro Binder.extractByType_impl[T ,Rx[MouseEvent]]
//    def extractKeyboardEvents:Map[String,Rx[KeyboardEvent]] = macro Binder.extractByType_impl[T ,Rx[KeyboardEvent]]


}
