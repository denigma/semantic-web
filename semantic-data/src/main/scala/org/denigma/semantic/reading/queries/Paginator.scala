package org.denigma.semantic.reading.queries

import com.bigdata.rdf.sparql.ast.SliceNode
import org.denigma.semantic.reading._
import org.denigma.semantic.reading.questions._
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading.constructs._
import scala.util.Try


/*
adds paginated mode of qurieing
 */
trait PaginatedQueryManager[T] extends QueryManager[T]{

    def slice[TQ <:AnyQuery](q:TQ,offset:Int = 0, limit:Int) = {
      val cont =  q.getASTContainer
      val ast = cont.getOriginalAST
      if(offset>0 && limit>0)
        ast.setSlice(new SliceNode())
      else
        lg.error(s"bad slice: ( $offset , $limit)")
      q
    }

    def paginated(query:String,offset:Int,limit:Int):AnyQuerying[T] =
    {
      case (str:String,con:ReadConnection,q:AskQuery)=> this.askHandler(str,con,slice(q,offset,limit))

      case (str:String,con:ReadConnection,q:SelectQuery)=>this.selectHandler(str,con,slice(q,offset,limit))

      case (str:String,con:ReadConnection,q:ConstructQuery)=>this.constructHandler(str,con,slice(q,offset,limit))

      case _ => throw new NoSuchElementException("no such query exception")

    }

    def query(str:String,offset:Int,limit:Int): Try[T] =  this.anyQuery(str,paginated(str,offset,limit))
}


trait Paginator extends PaginatedQueryManager[QueryResultLike] with JsonSelect with JsonAsk with JsonConstruct