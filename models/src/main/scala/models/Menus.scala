package models

import org.denigma.rdf.WebIRI


//object Menu {
//  def apply(uri:WebIRI, label:String, child:MenuItem*): Menu = {
//    new Menu(uri,label,child:_*)
//  }
//
//}

case class TestMenu(uri:WebIRI,label:String) //extends MenuItemLike

case class Menu(uri:WebIRI,label:String, children: List[MenuItem]) extends MenuItemLike

case class MenuItem(uri:WebIRI,label:String) extends MenuItemLike

trait MenuItemLike{
  val uri:WebIRI
  val label:String
}