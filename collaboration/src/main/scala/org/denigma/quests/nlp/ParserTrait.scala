package org.denigma.quests.nlp

import org.parboiled.scala._
import org.parboiled.matchers.Matcher
import scala.Some
import org.parboiled.errors.{ErrorUtils, ParsingException}

/**
 * Created with IntelliJ IDEA.
 * Member: AntonKulaga
 * Date: 1/19/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
trait ParserTrait[RET] {

  //<editor-fold desc="abstract">
  def rule[T <: Rule](block: => T)(implicit creator: Matcher => T): T

  def Expression:Rule1[RET]
  //</editor-fold>

 // val buildParseTree = true

  //def InputLine = rule { Expression ~ EOI }

  def parse(expression: String): RET = {
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
