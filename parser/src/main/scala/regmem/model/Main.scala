package regmem.model

import com.google.common.base.MoreObjects

import scala.xml.Node


object Categories {
  val values = Map(
    1 -> "Directorships",
    2 -> "Remunerated employment, office, profession etc.",
    3 -> "Clients",
    4 -> "Sponsorship or financial or material support",
    5 -> "Gifts, benefits and hospitality (UK)",
    6 -> "Overseas visits",
    7 -> "Overseas benefits and gifts",
    8 -> "Land and Property",
    9 -> "Registrable shareholdings",
    10 -> "Loans and other controlled transactions",
    11 -> "Miscellaneous"
  )
}





class Category(n: Node) {
  lazy val categoryType = (n \ "@type").text.toInt
  lazy val name = Categories.values(categoryType)

  lazy val records = (n \ "record").map(Record.parseXml)

  override def toString: String = MoreObjects.toStringHelper(this.getClass)
    .add("type", categoryType)
    .add("name", name)
    .toString
}

class Member(regmem: Node) {
  lazy val name = regmem \ "@membername"
  lazy val personid = regmem \ "@personid"
  lazy val memberid = regmem \ "@memberid"

  lazy val date = regmem \ "@date"

  lazy val categories = (regmem \ "category") map (n => new Category(n))

  override def toString: String = MoreObjects.toStringHelper(this.getClass)
    .add("name", name)
    .add("personid", personid)
    .add("memberid", memberid)
    .add("date", date)
    .toString
}

