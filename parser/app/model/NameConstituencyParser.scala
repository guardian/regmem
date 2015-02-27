package model

import org.parboiled2._
import org.parboiled2.CharPredicate._

case class NameAndConstituency(surname: String, forename: String, constituency: String)

class NameConstituencyParser(val input: ParserInput) extends Parser {
  def NameConstituency = rule { Surname ~ ", " ~ Forename ~ '(' ~ Constituency ~ optional(')') ~ EOI  ~> NameAndConstituency }

  def Surname = rule { capture(oneOrMore(Printable -- ',')) }

  def Forename = rule { capture(oneOrMore(Printable -- '(')) }

  def Constituency = rule { capture(oneOrMore(Printable -- '(' -- ')') ~ optional(Qualifier)) }

  def Qualifier = rule { '(' ~ oneOrMore(Printable -- ')') ~ ')' }
}
