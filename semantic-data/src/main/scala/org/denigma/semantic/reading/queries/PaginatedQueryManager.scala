package org.denigma.semantic.reading.queries

import com.bigdata.rdf.sparql.ast.SliceNode
import org.denigma.semantic.reading._
import org.denigma.semantic.reading.questions._
import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading.constructs._
import scala.util.Try
import org.denigma.semantic.reading.modifiers.{Paginator, Slicer}
import org.openrdf.model.Value


/*
adds paginated mode of qurieing
 */
trait PaginatedQueryManager[T] extends QueryManager[T] with Paginator[T]{


    /*
    paginated handler, that figures out the type of a query and than applies appropriate paginated handler
     */
    def paginated(query:String,offset:Long,limit:Long):AnyQuerying[T] =
    {
      case (str:String,con:ReadConnection,q:AskQuery)=> this.askHandler(str,con,slice[AskQuery](q,offset,limit))

      case (str:String,con:ReadConnection,q:SelectQuery)=>this.selectHandler(str,con,slice[SelectQuery](q,offset,limit))

      case (str:String,con:ReadConnection,q:ConstructQuery)=>this.constructHandler(str,con,slice[ConstructQuery](q,offset,limit))

      case _ => throw new NoSuchElementException("no such query exception")

    }

  /*
  any readonly query with pagination
   */
    def query(str:String,offset:Long,limit:Long): Try[T] =  this.anyQuery(str,paginated(str,offset,limit))

}

