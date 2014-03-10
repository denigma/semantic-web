package org.denigma.semantic.reading.modifiers

import org.denigma.semantic.reading.queries._
import com.bigdata.rdf.sparql.ast.SliceNode

trait Slicer{
  def slice[TQ <:AnyQuery](q:TQ,offset:Long, limit:Long,rewrite:Boolean = false): TQ = {
    val cont =  q.getASTContainer
    val ast = cont.getOriginalAST
    if(rewrite || !ast.hasSlice)
    {

      if(limit>0)
      {
        if(offset>0){
          ast.setSlice(new SliceNode(offset,limit))
        }
        else ast.setSlice(new SliceNode(0,limit))
      }
      else{
        if(offset>0){
          ast.setSlice(new SliceNode(offset,Long.MaxValue))
        }

      }
      cont.setOriginalAST(ast)
    }

    q
  }
}
