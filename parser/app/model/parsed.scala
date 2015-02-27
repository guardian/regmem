package model

import org.joda.time.LocalDate

case class Category
  (
  id: Int,
  lineItems: Seq[Item]
) {
  def name = Categories.values(id)
}

case class Item
  (
  description: String,
  date: Option[LocalDate] = None,
  registered: Option[LocalDate] = None,
  amount: Option[BigDecimal] = None
)

object CategoryParser{
  def apply(c: RawCategory): Category = {
    c.id match {
      case 1 | 2 | 8 =>
        Category(
          c.id,
          parentChildSpacerParser(c.lineItems.toList)
        )

      case other =>
        Category(
          c.id,
          Seq(Item("[don't know how to parse category " + c.id + " yet]"))
        )
    }
  }

  val REGISTERED = """\((Registered|Updated) .*\)""".r

  def dropRegistered(s: String) = REGISTERED.replaceAllIn(s, " ").trim

  def parentChildSpacerParser(rawItems: List[RawLineItem]): List[Item] = {
    rawItems match {
      case Nil => Nil

      case head :: rest if head.indent == "spacer" =>
        parentChildSpacerParser(rest)

      case head :: rest if head.indent == "indent" =>
        val (children, others) = rest.span(_.indent != "spacer")

        if (children.isEmpty) {
          Item(description = dropRegistered(head.value)) :: parentChildSpacerParser(others)
        } else {
          val allThese = children.map(c =>
            Item(description = s"${dropRegistered(head.value)} ${dropRegistered(c.value)}")
          )

          allThese ::: parentChildSpacerParser(others)
        }

      case other =>
        sys.error("don't understand what to do with " + other)
    }
  }

}