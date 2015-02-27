package regmem.model

import java.io.File

import org.joda.time.LocalDate
import org.jsoup.Jsoup
import org.parboiled2.ParseError
import org.scalatest._

class RecordTest extends FlatSpec with Matchers {

  it should "parse simple entry from dianne abbott" in {
    val html = Jsoup.parse(new File("abbott_diane.htm"), "UTF-8")
  }


}
