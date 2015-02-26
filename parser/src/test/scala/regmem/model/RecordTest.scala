package regmem.model

import org.joda.time.LocalDate
import org.parboiled2.ParseError
import org.scalatest._

class RecordTest extends FlatSpec with Matchers {

  it should "parse simple entry from dianne abbott" in {
    val src = <record>
      <item>
        <span style="        color: 000000;       ">23 January 2014, received fee of £2,000 for speaking at Citywealth Power Women Lunch 2014, Address: 26 Bryanston Mews East, London W1H 2DB. Hours: 3 hrs. </span>
        <span style="        color: 000000;       "><em>(Registered 4 February 2014)</em></span>
      </item>
    </record>

    val record = Record.parseXml(src)

    record.registeredDate shouldBe new LocalDate(2014, 2, 4)


  }

  def debug(path: String): Unit = {
    new RecordParser(path).RegisteredDate.run()
      .recoverWith {
      case p: ParseError =>
        println(path)
        println(" " * (p.position.column-2) + "^")
        println(s"traces: ${p.formatTraces}")
        println("expected: " + p.formatExpectedAsString)
        println("position: " + p.position)
        throw p
    }
  }


  it should "dosko" in {
    val data = "some text (Registered 4 February 2014) blah"
    debug(data)

    val result = new RecordParser(data).RegisteredDate.run()

    println(result)
  }

}
