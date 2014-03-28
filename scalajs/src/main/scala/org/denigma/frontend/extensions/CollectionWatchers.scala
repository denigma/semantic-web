package org.denigma.frontend.extensions


import scala.collection.immutable._
import rx._
import rx.ops._

//NOTE THIS CODE IS NOT TESTED YET
trait RxCollectionOps {
  /**
   * Watch changes in the collection
   * @param col
   * @tparam T
   */
  implicit class Watcher[T](col:Rx[List[T]])
  {
    //val red: Rx[(List[T], List[T])] = col.reduce((_,_))
    /*
    It would be nice to have a zip or buffer function as reduce does not allow me to map the result to something else
     */

    var previous: List[T] = col.now
    val red = Rx{
      val old = previous
      previous = col.filter(_!=this.previous)() //TODO: maybe dangerous!
      (old,previous)
    }


    val updates = red.map{case (prev,cur)=>
      val removed = prev.diff(cur)
      val inserted = cur.diff(prev)
      val unPre: List[T] = prev.filterNot(removed.contains)
      val unCur = cur.filterNot(inserted.contains)
      assert(unPre.size==unCur.size)
      val swapped: List[Swap[T]] = for {
        p<-unPre
        c<-unCur
        if p!=c
      } yield Swap(unPre.indexOf(c),unCur.indexOf(c), c)
      CollectionUpdate[T](removed,inserted,swapped)
    }

    def watcher: Rx[CollectionUpdate[T]] = updates
  }
}
case class Swap[T](from:Int,to:Int,item:T)
case class CollectionUpdate[T](removed:List[T],inserted:List[T],prev:List[Swap[T]])
