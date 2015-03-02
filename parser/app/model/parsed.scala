package model

import org.joda.time.LocalDate

case class Category
  (
  id: Int,
  lineItems: Seq[Item],
  notImplemented: Boolean
) {
  def cssClass =
    if (notImplemented) "text-muted"
    else "text-success"

  def name = Categories.values(id)
}

case class Item
  (
  description: String,
  date: Option[LocalDate] = None,
  registered: Option[LocalDate] = None,
  amount: Option[BigDecimal] = None
) {
  def cssClass = ""
}

object CategoryParser{
  def apply(c: RawCategory): Category = {
    c.id match {
      case 1 | 2 | 8 | 11 =>
        val items = parentChildSpacerParser(c.lineItems.toList)
        Category(
          c.id,
          items,
          notImplemented = false
        )

      case other =>
        Category(
          c.id,
          Nil,
          notImplemented = true
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

      case head :: rest /* if head.indent == "indent" */ =>
        val (children, others) = rest.span(_.indent != "spacer")

        if (children.isEmpty) {
          Item(description = dropRegistered(head.value), amount = head.mostCash) :: parentChildSpacerParser(others)
        } else {
          val allThese = children.map(c =>
            Item(description = s"${dropRegistered(head.value)} ${dropRegistered(c.value)}", amount = c.mostCash orElse head.mostCash)
          )

          allThese ::: parentChildSpacerParser(others)
        }

    }
  }

}