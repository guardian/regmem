package regmem

import org.parboiled2._
import org.parboiled2.CharPredicate._

class CashMoneyParser(val input: ParserInput) extends Parser {
  def Line = rule { NotCash ~ zeroOrMore(Cash).separatedBy(NotCash) ~ zeroOrMore(All -- EOI) ~ EOI }

  def NotCash = rule { zeroOrMore(!Cash ~ ANY) }

  def Cash = rule { '£' ~ Pounds ~ optional('.' ~ (Pence)) ~>
    ((pounds, pence) => pounds * 100 + pence.getOrElse(0L))
  }

  def Pence = rule { capture(2.times(Digit)) ~>
    (_.mkString.toLong) }

  def Pounds = rule { oneOrMore(capture(Digit) | (',' ~ push("")) ) ~> (_.mkString.toLong)}
}
