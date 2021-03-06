package model

import model.CashMoneyParser
import org.jsoup.nodes.{Document, Element}

import scala.util.Try

// these classes represnet the data as initially parsed from the html

case class RawLineItem
  (
  value: String,
  indent: String,
  mostCash: Option[BigDecimal]
)

case class RawCategory
  (
  id: Int,
  lineItems: Seq[RawLineItem]
) {
  def name = Categories.values(id)
  def totalCash = lineItems.flatMap(_.mostCash).sum
}

case class RawMpInfo
  (
  name: String,
  categories: Seq[RawCategory]
)  {
  def totalCash = categories.map(_.totalCash).sum
}

object RawParser {
  def apply(html: Document): RawMpInfo = {
    // only interested in the main text block
    val mainText = html.select("#mainTextBlock")

    val h2 = mainText.select("h2").first()
    val mpName = h2.text()

    def remainingSiblings(e: Element): List[Element] = Option(e.nextElementSibling()) match {
      case None => Nil
      case Some(next) => next :: remainingSiblings(next)
    }

    val siblings: List[Element] = remainingSiblings(h2).filterNot(_.hasClass("prevNext"))

    def extractLineItems(elements: List[Element]): Seq[RawLineItem] = {
      elements map { e =>
        RawLineItem(e.text, e.className, new CashMoneyParser(e.text).Line.run().get.sorted.lastOption)
      }
    }

    def processNextCategory(remaining: List[Element]): List[RawCategory] = {
      remaining match {
        case Nil => Nil

        case head :: rest if head.tagName == "h3" =>
          val (thisCatValues, nextCat) = remaining.tail.span(_.tagName != "h3")

          val name = head.text
          val categoryNumber = Try(name.trim.takeWhile(_.isDigit).toInt)

          categoryNumber map { num =>
            val thisCat = RawCategory(num, extractLineItems(thisCatValues))
            thisCat :: processNextCategory(nextCat)
          } getOrElse {
            println("ignoring dodgy h3 line " + name)
            processNextCategory(nextCat)
          }

        case head :: rest if head.text == "Nil." =>
          processNextCategory(rest)

        case head :: rest if head.hasClass("spacer") =>
          processNextCategory(rest)

        case other =>
          sys.error("argh! didn't understand " + other)
      }
    }

    RawMpInfo(
      name = mpName,
      categories = processNextCategory(siblings)
    )

  }
}