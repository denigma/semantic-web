package org.denigma.genes


import scala.Predef._


import akka.pattern.ask


import play.api.libs.iteratee._

import scala.concurrent.duration._

import org.scalatest._


import akka.testkit._

import akka.actor._
import org.denigma.actors.messages._
import scala.util.Success
import play.api.libs.json._
import  scala.language.postfixOps
import org.denigma.genes.mock.MockGeneMaster
import org.denigma.data.Lookup
import org.denigma.actors.base.MainSpec

class SearchSpec(_system: ActorSystem) extends MainSpec[MockGeneMaster](_system) //extends TestKit(_system) with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender
{
  def this() = this(ActorSystem("MySpec"))

  "Search" should
    {


      val master = TestActorRef[MockGeneMaster]
      val masterActor = master.underlyingActor

      var channels = Vector.empty[(Iteratee[JsValue, _], Enumerator[JsValue])]



      val l  = new Lookup()

      l.add("zero","1one1")
      l.add("one","one value first")
      l.add("one","one value first")
      l.add("two","one value second")
      l.add("two","two value first")
      l.add("two","two value scd")
      l.add("third","one")
      /*
      val r = l.suggestKeysByValues("one")
      r.size.shouldEqual(4)
      val (first,second,third, fourth) = (r.head,r.tail.head,r.tail.tail.head,r.tail.tail.tail.head)
      first shouldEqual ("one", mutable.Set("third"))
      second shouldEqual ("1one1", mutable.Set("zero"))
      third shouldEqual ("one value first",mutable.Set("one"))
      fourth shouldEqual ("one value second",mutable.Set("two"))
      */


      addUser(user1,password1)
      addUser(user2,password2)
      addUser(user3,password3)



      "stream results" in
      {

      }


    }
}
