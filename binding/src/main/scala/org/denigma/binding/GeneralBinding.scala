package org.denigma.binding

import rx._
import org.scalajs.dom._
import scala.collection.mutable
import scala.collection.immutable._
import org.scalajs.dom
import org.denigma.binding.macroses.{BooleanRxMap, ClassToMap, StringRxMap}
import org.denigma.extensions._

import dom.extensions._
import scala.Some


/**
 * Binds separate properties to HTML nodes
 */
trait GeneralBinding  extends JustBinding with VisibilityBinder with ClassBinder with PropertyBinder{

  def bools:Map[String,Rx[Boolean]]
  def strings:Map[String,Rx[String]]
  //def doubles:Map[String,Rx[Double]]


  //  def extractBooleans[T]:Map[String,Rx[Boolean]] = macro Binder.booleanBindings_impl[T]
  //  def extractStrings[T]:Map[String,Rx[String]] = macro Binder.stringBindings_impl[T]
  //  def extractDoubles[T]:Map[String,Rx[Double]] = macro Binder.doubleBindings_impl[T]


  def extractAll[T: ClassToMap](t: T): Map[String, Any] =  implicitly[ClassToMap[T]].asMap(t)
  def extractStringRx[T: StringRxMap](t: T): Map[String, Rx[String]] =  implicitly[StringRxMap[T]].asStringRxMap(t)
  def extractBooleanRx[T: BooleanRxMap](t: T): Map[String, Rx[Boolean]] =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)




  def makeTextHandler(el:HTMLElement,par:Rx[String]):(KeyboardEvent)=>Unit = this.makeEventHandler(el,par){ (ev,v,elem)=>
    if(elem.textContent.toString!=v.now) {
      v()=elem.textContent.toString
    }
  }



  //TODO: rewrite
  def bindProperties(el:HTMLElement,ats:mutable.Map[String, dom.Attr]): Unit = for {
    (key, value) <- ats
  }{
    key.toString match {

      case "showif" => this.showIf(el,value.value,el.style.display)
      case "hideif" => this.hideIf(el,value.value,el.style.display)
      case "class" => this.bindClass(el,value.value)
      case str if str.startsWith("class-")=> str.replace("class-","") match {
        case cl if cl.endsWith("-if")=>
           this.classIf(el,cl.replace("-if",""),value.value)
        case cl if cl.endsWith("-unless")=>
          this.classUnless(el,cl.replace("-unless",""),value.value)
        case _ =>
          dom.console.error(s"other class bindings are not implemented yet for $str")

        }

      case bname if bname.startsWith("bind-")=>this.bindAttribute(el,key.replace("bind-",""),value.value,this.strings)
      case "bind" => this.bindProperty(el,key,value)
      case "html" => this.bindInnerHTML(el,key,value)
      case _ => //some other thing to do
    }
  }




//  protected def bindTextArea(el:HTMLElement,key:String,att:Attr,mp:Map[String,Rx[String]]):PartialFunction[(String,String),Unit] = {
//    case ("bind", "textarea") =>
//      mp.get(att.value.toString).foreach {
//        str =>
//          el.onkeyup = this.makePropHandler(el, str, "value")
//          this.bindText(el, key, str)
//      }
//  }

  /**
   * @param el element
   * @param key key
   * @param value value
   * @param mp map
   */
  def bindAttribute(el:HTMLElement,key:String,value:String,mp:Map[String,Rx[String]]): Unit =  mp.get(value) match
  {
    case Some(str)=>
      this.bindRx(key, el: HTMLElement, str) {
        (el, value) =>
          //dom.console.info((key -> value.toString).toAtt.toString)
          el.attributes.setNamedItem((key -> str.now).toAtt)
          el.dyn.updateDynamic(key)(str.now) //TODO: check if redundant
      }

    case _=>  dom.console.error(s"unknown binding for $key with attribute $value")

  }





}

