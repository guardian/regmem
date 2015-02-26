package regmem.model

import org.joda.time.LocalDate
import org.parboiled2.{Parser, ParserInput}

import scala.xml.NodeSeq

case class Record
  (
  registeredDate: LocalDate
)

object Record {
   def parseXml(ns: NodeSeq): Record = {
     println("parsing " + ns.text)
     val parser = new RecordParser(ns.text)

     val date = parser.RegisteredDate.run()

     Record(registeredDate = date.get)
   }
 }


class RecordParser(val input: ParserInput) extends Parser {
  import org.parboiled2.CharPredicate._

  def NotADate = rule { oneOrMore(All -- '(' -- EOI) }

  def RegisteredDate = rule {
    optional(NotADate) ~ "(Registered " ~ Date ~ ")"
  }

  def Date = rule {
    (Day ~ ' ' ~ Month ~ ' ' ~ Year) ~>
      ((d, m, y) => new LocalDate(y, m, d))
  }

  def Year = rule { capture(4 times Digit) ~> (y => y.toInt) }

  def Day = rule { capture(1 to 2 times Digit) ~> (d => d.toInt) }

  def Month = rule {
    Map(
      "January" -> 1, "February" -> 2, "March" -> 3, "April" -> 4, "May" -> 5, "June" -> 6,
      "July" -> 7, "August" -> 8, "September" -> 9, "October" -> 10, "November" -> 11, "December" -> 12
    )
  }


}
