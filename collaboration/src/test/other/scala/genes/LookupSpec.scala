package org.denigma.genes

import org.scalatest._
import org.scalatest.WordSpec

import scala.collection.mutable
import org.denigma.data.Lookup

/**
 * Created with IntelliJ IDEA.
 * User: antonkulaga
 * Date: 4/29/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
class LookupSpec extends WordSpec with Matchers {

  "Lookup" should {
    "sort well" in {

      val l  = new Lookup()

      l.add("zero","1one1")
      l.add("one","one value first")
      l.add("one","one value first")
      l.add("two","one value second")
      l.add("two","two value first")
      l.add("two","two value scd")
      l.add("third","one")

      val r = l.suggestKeysByValues("one")


      r.size.shouldEqual(4)
      val (first,second,third, fourth) = (r.head,r.tail.head,r.tail.tail.head,r.tail.tail.tail.head)
      first shouldEqual ("one", mutable.Set("third"))
      second shouldEqual ("1one1", mutable.Set("zero"))
      third shouldEqual ("one value first",mutable.Set("one"))
      fourth shouldEqual ("one value second",mutable.Set("two"))




    }
  }

}
