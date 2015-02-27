package model

import org.parboiled2._
import org.parboiled2.CharPredicate._

class CashMoneyParser(val input: ParserInput) extends Parser {
  def Line = rule { NotCash ~ zeroOrMore(Cash).separatedBy(NotCash) ~ zeroOrMore(All -- EOI) ~ EOI }

  def NotCash = rule { zeroOrMore(!Cash ~ ANY) }

  def Cash = rule { '£' ~ Pounds ~ capture(optional('.' ~ (Pence))) ~>
    ((pounds, pence) => BigDecimal(pounds.mkString + pence))
  }

  def Pence = rule { 2.times(Digit) }

  def Pounds = rule { oneOrMore(capture(oneOrMore(Digit))).separatedBy(',') ~> (_.mkString)}
}
