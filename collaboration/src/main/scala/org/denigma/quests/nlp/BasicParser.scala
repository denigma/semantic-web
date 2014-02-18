package org.denigma.quests.nlp

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}
import org.parboiled.parserunners.ParseRunner
import org.parboiled.parserunners.ParseRunner._
import org.denigma.semantic.query.mocks._
import util.Either.LeftProjection
import org.parboiled.matchers.OneOrMoreMatcher

trait BasicParser extends Parser{

  def Chars = oneOrMore(Char)

  def Char = noneOf("{}")

  def Number =  oneOrMore("0" - "9")
}
