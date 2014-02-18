package org.denigma.quests.nlp

import org.scalatest._
import org.denigma.semantic.query.mocks.{ComplexMapper, UserTest}
import org.parboiled.scala.ParsingResult

/**
 * This class test the peg parser for properties insertions
 * */
class BindingParserSpec extends WordSpec with ShouldMatchers
{

  "Binding parser stack" should
  {
    "Tests binding parserBinding parsing" in
    {
      val mapper = new ComplexMapper
      {
        val user = new UserTest("Daniel","Wutske")
        var props: Map[String, Any] = Map[String,Any]("first"->"Name","second"->"Surname","third"->"Lastname","user"->user)
      }
      val parser = new BindingParser(mapper,mapper.get)

      val input = "Give my {first} and my {second} with my {third} to {user.name} {user.surname}!"

      println(parser.parse(input))
      val res: ParsingResult[String] = parser.run(input)
      res.result match
      {
        case Some(str)=>str should equal("Give my Name and my Surname with my Lastname to Daniel Wutske!")
        case None=>fail("Binding parsing failed")

      }

    }
  }
}
