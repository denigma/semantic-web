package org.denigma.semantic.reading.modifiers


import scala.util.Try
import com.bigdata.rdf.sparql.ast._
import com.bigdata.rdf.model._
import org.denigma.semantic.reading.selections._
import org.openrdf.model.{URI, Value}
import org.openrdf.model.impl._
import com.bigdata.rdf.internal._
import org.denigma.semantic.reading.ReadConnection
import com.bigdata.rdf.internal.impl._
import com.bigdata.rdf.internal.constraints.{StrBOp, RegexBOp}
import com.bigdata.bop.Constant

trait ASTHelper {

  /*
  creates constant node from string
   */
  def literalConst(str:String)(con:ReadConnection) = {
    val lit = con.getValueFactory.createLiteral(str)
    val term = TermId.mockIV(VTE.valueOf(lit)).asInstanceOf[TermId[BigdataLiteral]]
    term.setValue(lit)
    new ConstantNode(term)
  }

  /*
  creates constant node from string
   */
  def registerTerms(params:BigdataValue*)(con:ReadConnection) = {
    val lex = con.getTripleStore.getLexiconRelation
    val arr = params.toArray
    lex.addTerms(arr,arr.length,true)
    //com.bigdata.rdf.internal.constraints.StrBOp
    params

  }

  val fn:FunctionNode = null


  def varNode(str:String) = new VarNode(str)

  def var2Str(variable:String): FunctionNode = new FunctionNode(FunctionRegistry.STR,null,new VarNode(variable))

  def matchRegex(variable:String,value:String)(con:ReadConnection) = {
    new FunctionNode (FunctionRegistry.REGEX,null,this.var2Str(variable),this.literalConst(value)(con))

  }
  def filterRegex(variable:String,value:String)(con:ReadConnection) = new FilterNode(new FunctionNode (FunctionRegistry.REGEX,null,this.var2Str(variable),this.literalConst(value)(con)))


}


/*
class that can do binding
 */
trait Binder[T] extends SelectReader with Slicer with ASTHelper
{
  def alice(q:SelectQuery,con:ReadConnection)
  // regex("Alice", "^ali", "i") -> true
  {
    val vf = con.getValueFactory
    val expected: Boolean = true
    val v = DummyConstantNode.toDummyIV(vf.createLiteral("Alice"))
    val pattern = DummyConstantNode.toDummyIV(vf.createLiteral("^ali"))
    val flags = DummyConstantNode.toDummyIV(vf.createLiteral("i"))
    val c: Constant[IV[_ <: BigdataValue, _]] = new Constant(v)
    val s = new VarNode("hellov")


  }

  /*
  binds the query to set of values
   */
  def bind(con:ReadConnection,q:SelectQuery,params:Map[String,String]) = {
    val f: BigdataValueFactory = con.getValueFactory
    params.foreach{
      case (key,value) if value.contains("_:")=> q.setBinding(key,f.createBNode(value.replace("_:","")))
      case (key,value) if value.contains(":")=> q.setBinding(key,f.createURI(value))
      case (key,value) => f.createLiteral(value)
    }
    q
  }

  /*
  todo: complete
   */
  def search(con:ReadConnection,q:SelectQuery,key:String,value:String) = {
    val cont = q.getASTContainer
    val ast = cont.getOriginalAST
    val where = ast.getWhereClause


    //pat.addChild(new StatementPatternNode())
    //pat.addChild()
    q
  }

  /*
  creates AST nodes out of bigdata values and return a map of value->termnode
   */
  def makeNodes(q:SelectQuery,terms:BigdataValue*):Map[Value,TermNode] = {
    val lex = q.getTripleStore.getLexiconRelation
    lex.addTerms(terms.toArray,terms.size,true)
    terms.map{
      case (value:BigdataValue)=>
        val iv = value.getIV.asInstanceOf[com.bigdata.rdf.internal.IV[com.bigdata.rdf.model.BigdataValue, _]]
        iv.setValue(value)
        val cn = new ConstantNode(iv)
        (value,cn)
    }.toMap
  }

  protected def bindedHandler(str:String,binds:Map[String,String]):SelectHandler[T]

  protected def bindedHandler(str:String,binds:Map[String,String],offset:Long,limit:Long):SelectHandler[T]

  /*
  sends query with binding
   */
  def bindedQuery(str:String,binds:Map[String,String],offset:Long,limit:Long): Try[T] =  this.selectQuery(str,bindedHandler(str,binds,offset,limit))


  def bindedQuery(str:String,binds:Map[String,String]): Try[T] = this.selectQuery(str,this.bindedHandler(str,binds))

}

/*
TODO: experiment with different things
 */
//  private def bindQuery(q:BigdataSailTupleQuery, bindings:(String,BigdataValue)*) = {
//    val cont: ASTContainer = q.getASTContainer
//
//    val ast = cont.getOriginalAST
//    val lex = q.getTripleStore.getLexiconRelation
//    val terms = bindings.map(_._2)
//    lex.addTerms(terms.toArray,terms.size,true)
//    val params: Map[String, ConstantNode] = bindings.map{
//      case (key,value:BigdataValue)=>
//        val iv = value.getIV.asInstanceOf[com.bigdata.rdf.internal.IV[com.bigdata.rdf.model.BigdataValue, _]]
//        iv.setValue(value)
//        val cn = new ConstantNode(iv)
//        (key,cn)
//    }.toMap
//
//
//    val pat = ast.getGraphPattern
//
//
//    val ptns = pat.getChildren.toList
//    pat.addChild(new StatementPatternNode())
//    ptns.map{case stn:StatementPatternNode=>stn}
//
//    ptns.view.filter(v=>v.isInstanceOf[StatementPatternNode]).foreach{
//      case stn:StatementPatternNode=>
//
//        Map(0->stn.s(),1->stn.p(),2->stn.o()).foreach{
//          case (i,v:VarNode)=>
//
//            val n: String = v.getValueExpression.getName
//            if(params.contains(n))   stn.setArg(i,params(n))
//          case _ => // skip
//        }
//    }
//
//    cont.setOriginalAST(ast)
//    q
//  }