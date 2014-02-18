package org.denigma.quests.nlp

import org.parboiled.scala._

/**
 * Created with IntelliJ IDEA.
 * Member: AntonKulaga
 * Date: 1/18/13
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}

/**
 * A parser for a simple calculator language supporting the 4 basic calculation types on integers.
 * The actual calculations are performed by inline parser actions using the nlp value stack as temporary storage.
 */
class SimpleCalculator extends Parser {

    def InputLine: _root_.org.parboiled.scala.Rule1[Int] = rule { Expression ~ EOI }

    def Expression: Rule1[Int] = rule {
      Term ~ zeroOrMore(
        "+" ~ Term ~~> ((a:Int, b) => a + b)
          | "-" ~ Term ~~> ((a:Int, b) => a - b)
      )
    }

    def Term: Rule1[Int] = rule {
      Factor ~ zeroOrMore(
        "*" ~ Factor ~~> ((a:Int, b) => a * b)
          | "/" ~ Factor ~~> ((a:Int, b) => a / b)
      )
    }

    def Factor: Rule1[Int] = rule { Number | Parens }

    def Parens: Rule1[Int] = rule { "(" ~ Expression ~ ")" }

    def Number: Rule1[Int] = rule { Digits ~> (_.toInt) }

    def Digits: _root_.org.parboiled.scala.Rule0 = rule { oneOrMore(Digit) }

    def Digit: Rule0 = rule { "0" - "9" }

    /**
     * The main parsing method. Uses a ReportingParseRunner (which only reports the first error) for simplicity.
     */
    def calculate(expression: String): Int = {
      val parsingResult = ReportingParseRunner(InputLine).run(expression)
      parsingResult.result match {
        case Some(i) => i
        case None => throw new ParsingException("Invalid calculation expression:\n" +
          ErrorUtils.printParseErrors(parsingResult))
      }
    }
}