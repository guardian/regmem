package model

import java.io.File

import org.joda.time.LocalDate
import org.jsoup.Jsoup
import org.scalatest._

class RecordTest extends FlatSpec with Matchers with OptionValues {

  it should "parse simple entry from dianne abbott" in {
    val html = Jsoup.parse(new File("abbott_diane.htm"), "UTF-8")

    val model = RawParser.apply(html)

    model.name shouldBe "ABBOTT, Diane (Hackney North and Stoke Newington)"

    model.categories.size shouldBe 4

    val employment = model.categories.find(_.id == 2).value

    val items = Seq(
     RawLineItem("Fees received for co-presenting BBC’s ‘This Week’ TV programme. Address: BBC Broadcasting House, Portland Place, London W1A 1AA: (Registered 4 November 2013, updated 13 March 2014)", "indent"),
     RawLineItem("30 January 2014, received £700. Hours: 3 hrs. (Registered 4 February 2014)", "indent2"),
     RawLineItem("6 March 2014, received £700. Hours: 3 hrs. (Registered 13 March 2014)", "indent2"),
     RawLineItem("20 March 2014, received £700. Hours: 3 hrs. (Registered 3 April 2014)", "indent2"),
     RawLineItem("1 May 2014, received £700.  Hours: 3 hrs. (Registered 14 May 2014)", "indent2"),
     RawLineItem("8 May 2014, received £700.  Hours: 3 hrs. (Registered 14 May 2014)", "indent2"),
     RawLineItem("5 June 2014, received £700.  Hours: 3 hrs. (Registered 19 June 2014)", "indent2"),
     RawLineItem("19 June 2014, received £700. Hours: 3 hrs. (Registered 19 June 2014)", "indent2"),
     RawLineItem("3 July 2014, received £700. Hours: 3 hrs. (Registered 7 July 2014)", "indent2"),
     RawLineItem("10 July 2014, received £700. Hours: 3 hrs. (Registered 4 August 2014)", "indent2"),
     RawLineItem("11 September 2014, received £700. Hours: 3 hrs. (Registered 21 October 2014)", "indent2"),
     RawLineItem("9 October 2014, received £700. Hours: 3 hrs. (Registered 21 October 2014)", "indent2"),
     RawLineItem("16 October 2014, received £700. Hours: 3 hrs. (Registered 21 October 2014)", "indent2"),
     RawLineItem("6 November 2014, received £700. Hours: 3 hrs. (Registered 2 December 2014)", "indent2"),
     RawLineItem("27 November 2014, received £700. Hours: 3 hrs. (Registered 2 December 2014)", "indent2"),
     RawLineItem("11 December 2014, received £700. Hours: 3 hrs. (Registered 14 January 2015)", "indent2"),
     RawLineItem("18 December 2014, received £700. Hours: 3 hrs. (Registered 14 January 2015)", "indent2"),
     RawLineItem("8 January 2015, received £700. Hours: 3 hrs. (Registered 14 January 2015)", "indent2"),
     RawLineItem("", "spacer"),
     RawLineItem("23 January 2014, received fee of £2,000 for speaking at Citywealth Power Women Lunch 2014, Address: 26 Bryanston Mews East, London W1H 2DB. Hours: 3 hrs. (Registered 4 February 2014)", "indent"),
     RawLineItem("", "spacer")
    )

    employment.lineItems.take(21) shouldBe items


    val p = new CategoryParser(employment)



  }

  it should "parse out simple items" in {
    val c = RawCategory(id = 2, lineItems = Seq(
      RawLineItem("23 January 2014, received fee of £2,000 for speaking at Citywealth Power Women Lunch 2014, Address: 26 Bryanston Mews East, London W1H 2DB. Hours: 3 hrs. (Registered 4 February 2014)", "indent")
    ))

    val p = new CategoryParser(c)
    p.items shouldBe Seq(
      Item(
        fullText = "23 January 2014, received fee of £2,000 for speaking at Citywealth Power Women Lunch 2014, Address: 26 Bryanston Mews East, London W1H 2DB. Hours: 3 hrs. (Registered 4 February 2014)",
        description = "Citywealth Power Women Lunch 2014, Address: 26 Bryanston Mews East, London W1H 2DB. Hours: 3 hrs.",
        date = new LocalDate(2014, 1, 23),
        registered = new LocalDate(2014, 2, 4),
        amount = BigDecimal(2000)
      )
    )

  }


  case class ParentItem()

  case class Item
    (
    fullText: String,
    description: String,
    date: LocalDate,
    registered: LocalDate,
    amount: BigDecimal
  )

  class CategoryParser(c: RawCategory) {
    lazy val items: Seq[Item] = Nil
  }

}
