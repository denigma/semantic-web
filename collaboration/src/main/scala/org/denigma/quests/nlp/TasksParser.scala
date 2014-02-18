package org.denigma.quests.nlp

import org.parboiled.scala._
import scala.Some
import org.parboiled.errors.{ErrorUtils, ParsingException}

/**
 * Created with IntelliJ IDEA.
 * UserStatus: antonkulaga
 * Date: 4/7/13
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
class TasksParser extends BasicParser
{

  override val buildParseTree = true

  def Delimiter = anyOf("\\.") | "->"


}