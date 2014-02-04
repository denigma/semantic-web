package models


import org.openrdf.model._
import play.api.Play
import org.openrdf.model.impl.URIImpl
import org.denigma.semantic.{SG, Prefixes}
import org.openrdf.model.vocabulary

object Menu{
  val child = new URIImpl(Prefixes ui "child")
}
/*
Menu class for models to load
TODO: complete the class
 */
class Menu(url:Resource) extends SemanticPage(url){



  var children: Seq[SemanticPage] = List.empty[SemanticPage]

  override def propertiesHandler(oldVal:Map[URI, Seq[Statement]],newVal:Map[URI, Seq[Statement]]):Unit  = {
    super.propertiesHandler(oldVal,newVal)
    children = List.empty[SemanticPage]
    newVal.get(Menu.child).foreach{v=>
//      this.children = v.view.map{
//        st=>st.getObject match {
//        case st:Statement if st.getPredicate => new SemanticPage(st.getObject)
//        case _=>play.api.Logger.error(s"unknown predicate")
//      }
    }


  }
}

object SemanticPage{

}

/*
Menu class for models to load
TODO: complete the class
 */
class SemanticPage(url:Resource) extends SemanticModel(url){

  override def addStatements(statements:Seq[Statement]) = {
    super.addStatements(statements)
    this.properties


  }

  var label:String = ""

  override def propertiesHandler(oldVal:Map[URI, Seq[Statement]],newVal:Map[URI, Seq[Statement]]):Unit  = {
    newVal.get(vocabulary.RDFS.LABEL).foreach{
      st=>
        if(!st.isEmpty)this.label=st.head.getObject.stringValue()
    }
  }


}



/*
Model with statements about one particular URL
 */
class SemanticModel(val url:Resource)
{
  SG.db.read{
    r=>
      val sts = r.getStatements(url,null,null,true)
      while(sts.hasNext)
      {

        sts.next()
      }
  }

  val parsers:List[(Statement,this.type)=>Boolean] = List.empty

  type OnChange = (Map[URI, Seq[Statement]],Map[URI, Seq[Statement]])=>Unit

  def addStatements(statements:Seq[Statement]) = {
    this.properties = this.properties++statements.view.filter(_.getSubject==url).groupBy[URI](st=>st.getPredicate)
    this.incoming = this.incoming++statements.view.filter(_.getObject==url).groupBy[URI](st=>st.getPredicate)
  }

  protected var _properties: Map[URI, Seq[Statement]] = Map.empty[URI, Seq[Statement]]
  def properties = _properties
  def properties_=(value:Map[URI, Seq[Statement]]) = {
    this.propertiesHandler(this._properties,value)
    this._properties = value
  }

  protected var _incoming= Map.empty[URI, Seq[Statement]]
  def incoming = _incoming
  def incoming_=(value:Map[URI, Seq[Statement]]) = {
    this.incomingHandler(_incoming,value)
    this._incoming = value
  }

  def propertiesHandler(oldVal:Map[URI, Seq[Statement]],newVal:Map[URI, Seq[Statement]]):Unit ={

  }
  def incomingHandler(oldVal:Map[URI, Seq[Statement]],newVal:Map[URI, Seq[Statement]]):Unit ={

  }


  def merge(other:SemanticModel): SemanticModel = if(other.url==url){
    val model = new SemanticModel(url)
    model.incoming = this.incoming++other.incoming
    model.properties = this.properties++other.properties
    model
  }
  else{
    play.api.Logger.error(s"cannot merge model of ${url.stringValue()} with model of ${other.url.stringValue()} because urls are different")
    this
  }


}