package org.denigma.quests.nlp

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.parboiled.parserunners.ParseRunner
import org.parboiled.parserunners.ParseRunner._
import org.denigma.semantic.query.mocks._
import util.Either.LeftProjection
import org.parboiled.matchers.OneOrMoreMatcher

object ParserHelper
{

  implicit val add1: (String) => String = (a)=>a

  implicit val red2: (String, String) => String = (a,b)=>a+b
  implicit val red3: (String, String,String) => String = (a,b,c)=>a+b+c


}


class BindingParser(where:ComplexObject, getter: (ComplexObject,String) => Either[String,ComplexObject])   extends BasicParser
{

  override val buildParseTree = true

  def InputLine =  Expression ~ EOI

  def Expression: Rule1[String] = Chars~>(_.toString)~Terms

  def Terms: _root_.org.parboiled.scala.ReductionRule1[String, String] = zeroOrMore("{"~Binding ~"}"~Chars~>(_.toString)~~>ParserHelper.red3 )

  def Variable:Rule1[String] = Chars~>{i=> getter(where,i) match
  {
    case Left(str:String)=> str
    case Right(obj:ComplexObject)=>obj.get(i).get.asInstanceOf[String]
  }
  }

  def Binding: Rule1[String] = Subject | Variable


  def Subject: Rule1[String] = Field~~>{getter(where,_)}~Properties~~>
    {
      sub=>sub match
      {
        case Left(str:String)=>str
        case _ =>"not_found"
      }
    }


  def Properties: ReductionRule1[Either[String, ComplexObject], Either[String, ComplexObject]] = oneOrMore(Property)


  def Property: ReductionRule1[Either[String, ComplexObject], Either[String, ComplexObject]] = Delimiter~Field~~>{
    (sub:Either[String, ComplexObject],field:String)=>sub match
    {
      case Left(str:String)=>getter(where,field)
      case Right(obj:ComplexObject)=>getter(obj,field)
    }
  }


  def prop(source:ComplexObject,name:String):String = getter(source,name) match
  {
    case Left(str:String)=> str
    case Right(obj:ComplexObject)=>prop(obj,name)
    case _=>"not_found"
  }

  def Field: Rule1[String] = oneOrMore(noneOf("{}().;/"))~>(_.toString)

  def Delimiter = anyOf("\\.") | "->"


  /*
  def Chars = oneOrMore(Char)

  def Char = noneOf("{}")

  def Number =  oneOrMore("0" - "9")
  */

  def parse(expression: String) = {
    val parsingResult = ReportingParseRunner(Expression).run(expression)
    parsingResult.result match {
      case Some(i) => i
      case None => throw new ParsingException("Invalid parsing expression:\n" +
        ErrorUtils.printParseErrors(parsingResult))
    }
  }

  def run(input: String) =  ReportingParseRunner(Expression).run(input)

  def tree(input: String)= org.parboiled.support.ParseTreeUtils.printNodeTree(run(input))

}
