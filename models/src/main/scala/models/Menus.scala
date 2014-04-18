package models

import org.denigma.rdf.IRI


//object Menu {
//  def apply(uri:IRI, label:String, child:MenuItem*): Menu = {
//    new Menu(uri,label,child:_*)
//  }
//
//}

case class TestMenu(uri:IRI,label:String) //extends MenuItemLike

case class Menu(uri:IRI,label:String, children: List[MenuItem]) extends MenuItemLike

case class MenuItem(uri:IRI,label:String) extends MenuItemLike

trait MenuItemLike{
  val uri:IRI
  val label:String
}