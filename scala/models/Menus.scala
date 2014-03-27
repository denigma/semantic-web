package models


object Menu {
  def apply(uri:WebIRI, label:String, child:MenuItem*): Menu = {
    new Menu(uri,label,child:_*)
  }
}
class Menu(val uri:WebIRI,val label:String, child:MenuItem*) extends MenuItemLike
{
  var children: List[MenuItem] = child.toList
}

case class MenuItem(uri:WebIRI,label:String) extends MenuItemLike

trait MenuItemLike{
  val uri:WebIRI
  val label:String
}