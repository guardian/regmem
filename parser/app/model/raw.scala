package model

import org.jsoup.nodes.{Document, Element}

import scala.util.Try

// these classes represnet the data as initially parsed from the html

case class RawLineItem
  (
  value: String,
  indent: String
)

case class RawCategory
  (
  id: Int,
  lineItems: Seq[RawLineItem]
)

case class RawMpInfo
  (
  name: String,
  categories: Seq[RawCategory]
)

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
        RawLineItem(e.text, e.className)
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