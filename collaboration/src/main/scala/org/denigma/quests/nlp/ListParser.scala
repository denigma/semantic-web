package org.denigma.quests.nlp

import org.parboiled.scala._
import scala.Some
import org.parboiled.errors.{ErrorUtils, ParsingException}

/**
 * Created with IntelliJ IDEA.
 * Member: AntonKulaga
 * Date: 1/18/13
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
class ListParser  extends Parser {
  override val buildParseTree = true

  def InputLine = rule { Expression ~ EOI }

  def Expression: Rule0 = ANY



  def Number = rule { oneOrMore("0" - "9") }


  def run(input: String) =  ReportingParseRunner(Expression).run(input)

  def tree(input: String)= org.parboiled.support.ParseTreeUtils.printNodeTree(run(input))
}
