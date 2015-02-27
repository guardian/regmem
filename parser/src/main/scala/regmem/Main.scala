package regmem

import java.io.File

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import regmem.model.Categories
import regmem.raw.RawParser


object Main extends App {
   println("hello")

  val html = Jsoup.parse(new File("abbott_diane.htm"), "UTF-8")
  //val html = Jsoup.parse(new File("blunkett_david.htm"), "UTF-8")
//  val html = Jsoup.parse(new File("/Users/gtackley/hack_regmem/mp-interests/mps-140922/yeo_tim.htm"), "UTF-8")

  val mp = RawParser(html)


  mp.categories foreach { c =>
    println(c.id + ": " + Categories.values(c.id))

    for (item <- c.lineItems) {
      println("  > " + item)
    }
    println()
  }

 }
