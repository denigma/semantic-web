package org.denigma.binding

import rx._
import org.scalajs.dom._
import scala.collection.mutable
import scala.collection.immutable._
import org.scalajs.dom
import org.denigma.binding.macroses.{TypeClass, BooleanRxMap, ClassToMap, StringRxMap}
import org.denigma.extensions._

import dom.extensions._
import scalatags.all._
import scala.Some
import scala.Some


/**
 * Binds separate properties to HTML nodes
 */
trait PropertyBinding  extends JustBinding{

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

  def makePropHandler[T<:Event](el:HTMLElement,par:Rx[String],pname:String):(T)=>Unit = this.makeEventHandler[T,String](el,par){ (ev,v,elem)=>
    elem \ pname  match {
      case Some(pvalue)=>
        if(v.now!=pvalue.toString) {
          v()=pvalue.toString
        }

      case None => dom.console.error(s"no attributed for $pname")
    }
  }



  def makeAttHandler[T<:Event](el:HTMLElement,par:Rx[String],atname:String):(T)=>Unit = this.makeEventHandler[T,String](el,par){ (ev,v,elem)=>
    elem.attributes.get(atname) match {
      case Some(att)=>
        if(v.now!=att.value.toString) {
        v()=att.value
      }

      case None => dom.console.error(s"no attributed for $atname")
    }
  }


  //TODO: rewrite
  def bindProperties(el:HTMLElement,ats:mutable.Map[String, dom.Attr]) = for {
    (key, value) <- ats
  }{
    key.toString match {

      case "showif" => this.showIf(el,value.value)
      case "hideif" => this.hideIf(el,value.value)
      case bname if bname.startsWith("bind-")=>this.bindAttribute(el,key.replace("bind-",""),value.value,this.strings)
      case "bind" => this.bindProperty(el,key,value)
      case _ => //some other thing to do
    }
  }

  def showIf(el:HTMLElement,show: String) = for ( b<-bools.get(show) )
    {
      val disp: String = el.style.display
      val rShow: Rx[String] = Rx{ if(b()) disp else "none"}
      el.style.display = rShow()
    }

  def hideIf(el:HTMLElement,hide: String) = for ( b<-bools.get(hide) )
    {
      val disp: String = el.style.display
      val rHide: Rx[String] = Rx{ if(!b()) disp else "none"}
      el.style.display = rHide()
    }


  //TODO: split into subfunctions
  /**
   * Binds property value to attribute
   * @param el Element
   * @param key name of the binding key
   * @param att binding attribute
   */
  def bindProperty(el:HTMLElement,key:String,att:dom.Attr): Unit = (key.toString,el.tagName.toLowerCase().toString) match
  {
    case ("bind","input")=>
      el.attributes.get("type").map(_.value.toString) match {
      case Some("checkbox") => this.bools.get(att.value.toString).foreach{b=>
        this.bindCheckBox(el,key,b)
      }
      case _ => this.strings.get(att.value).foreach{str=>
        el.onkeyup =this.makePropHandler[KeyboardEvent](el,str,"value")
        this.bindInput(el,key,str)
      }

    }
    case ("bind","textarea")=>
      this.strings.get(att.value.toString).foreach{str=>
        el.onkeyup = this.makePropHandler(el,str,"value")
        this.bindText(el,key,str)
      }

     case ("bind",other)=> this.strings.get(att.value.toString).foreach{str=>
       el.onkeyup = this.makePropHandler(el,str,"value")
       this.bindText(el,key,str)
     }

     case _=> dom.console.error(s"unknown binding for $key with attribute ${att.value}")

  }

//  protected def bindTextArea(el:HTMLElement,key:String,att:Attr,mp:Map[String,Rx[String]]):PartialFunction[(String,String),Unit] = {
//    case ("bind", "textarea") =>
//      mp.get(att.value.toString).foreach {
//        str =>
//          el.onkeyup = this.makePropHandler(el, str, "value")
//          this.bindText(el, key, str)
//      }
//  }

  def bindAttribute(el:HTMLElement,key:String,value:String,mp:Map[String,Rx[String]]) =  mp.get(value) match
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


  def bindInput(el:HTMLElement,key:String,str:Rx[String]) = this.bindRx(key,el:HTMLElement,str){ (el,value)=>
    if(el.dyn.value!=value) el.dyn.value=value
  }

  def bindText(el:HTMLElement,key:String,str:Rx[String]) = this.bindRx(key,el:HTMLElement,str){ (el,value)=>
    el.textContent = value
  }



  def bindCheckBox(el:HTMLElement,key:String,rx:Rx[Boolean]) = this.bindRx(key,el:HTMLElement,rx){ (el,value)=>
    el.attributes.setNamedItem( ("checked" -> value.toString ).toAtt )
  }






}

