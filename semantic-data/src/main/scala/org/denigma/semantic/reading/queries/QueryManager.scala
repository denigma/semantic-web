package org.denigma.semantic.reading.queries

import org.denigma.semantic.reading.selections._
import org.denigma.semantic.reading.questions._
import org.denigma.semantic.reading.constructs._
import org.denigma.semantic.reading._

/*
class that deals with all types of readonly quries and can find
 */
trait QueryManager[T] extends UniReader with ISelect[T] with IAsk[T] with IConstruct[T]
{

  def query(str:String) = anyQuery[T](str,anyQueryHandler)

  /*
  pattern matches the query and applies different handlers depending on its type
   */
  def anyQueryHandler:AnyQuerying[T] = {
    case (str:String,con:ReadConnection,q:AskQuery)=> this.askHandler(str,con,q)

    case (str:String,con:ReadConnection,q:SelectQuery)=>this.selectHandler(str,con,q)

    case (str:String,con:ReadConnection,q:ConstructQuery)=>this.constructHandler(str,con,q)

    case _ => throw new NoSuchElementException("no such query exception")

  }

}
