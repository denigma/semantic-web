package org.denigma.semantic.reading.modifiers


import scala.util.Try
import com.bigdata.rdf.sparql.ast._
import com.bigdata.rdf.model.BigdataValue
import org.denigma.semantic.reading.selections._
import org.openrdf.model.Value

/*
class that can do binding
 */
trait Binder[T] extends SelectReader with Slicer {


  def bind(q:SelectQuery,params:Map[String,Value]) = {
    params.foreach{case (key,value)=> q.setBinding(key,value)}
    q
  }

  /*
  todo: complete
   */
  def search(q:SelectQuery,key:String,value:String) = {
    val cont = q.getASTContainer
    val ast = cont.getOriginalAST
    val where = ast.getWhereClause
    val pat = where.getParentGraphPatternGroup
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

  protected def bindedHandler(str:String,params:Map[String,Value]):SelectQuerying[T]

  protected def bindedHandler(str:String,offset:Long,limit:Long,params:Map[String,Value]):SelectQuerying[T]

  /*
  sends query with binding
   */
  def bindedQuery(str:String,offset:Long,limit:Long,params:Map[String,Value]): Try[T] =  this.selectQuery(str,bindedHandler(str,offset,limit, params))


  def bindedQuery(str:String,params:Map[String,Value]): Try[T] = this.selectQuery(str,this.bindedHandler(str,params))

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