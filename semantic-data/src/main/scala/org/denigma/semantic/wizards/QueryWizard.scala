package org.denigma.semantic.wizards

import com.hp.hpl.jena.query.{Syntax, QueryFactory, Query}
import scala.util.{Failure, Try}
import org.denigma.semantic.data.QueryResultLike

/**
 * Created by antonkulaga on 2/14/14.
 */
abstract class QueryWizard extends SemanticWizard{


  def withLimit(q:Query,limit:Long, always:Boolean = true)= {
    if(limit>0)
      if(always || !q.hasLimit) q.setLimit(limit)
    q
  }

  def withOffset(q:Query,offset:Long, always:Boolean = true) = {
    if(offset>0)
      if(always || !q.hasOffset) q.setOffset(offset)
    q
  }

  /*
  it should be safe in future but now it is only limited
   */
  def safeQuery(str:String, limit:Long,offset:Long):Try[QueryResultLike] =
    this.alterQuery(str,limit,offset).map(db.query(_,str)).getOrElse(Failure(new RuntimeException(s"Unknown query type of $str")))

  /*
  adds limit and offset to the query
   */
  def alterQuery(str:String,limit:Long,offset:Long, sortVars:(String,Int)*): Try[String] = Try {
    if(limit<1 && offset <1) str else {
      val q: Query =   QueryFactory.create(str, Syntax.syntaxSPARQL_11)
      if(!q.hasOrderBy) {
        sortVars.foreach(kv=>q.addOrderBy(kv._1,kv._2))
      }
      if(q.isSelectType)  withOffset(withLimit(q,limit,always = false),offset,always = false).toString(Syntax.syntaxSPARQL_11) else str
    }
  }




  implicit class MagicQuery(q:Query) {

    def withLimit(limit:Long, always:Boolean = true)= {
      if(limit>0)
        if(always || !q.hasLimit) q.setLimit(limit)
      q
    }

    def withOffset(offset:Long, always:Boolean = true) = {
      if(offset>0)
              if(always || !q.hasOffset) q.setOffset(offset)
      q
    }



//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }
//
//    def withLimit(limit:Long, always:Boolean = true)= {
//      if(limit>0)
//        if(always || !q.hasLimit) q.setLimit(limit)
//      q
//    }

  }


}
