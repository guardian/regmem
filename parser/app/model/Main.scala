package model

import java.io.File

import org.jsoup.Jsoup


object Main extends App {
   println("hello")

  val html = Jsoup.parse(new File("abbott_diane.htm"), "UTF-8")
  //val html = Jsoup.parse(new File("blunkett_david.htm"), "UTF-8")
//  val html = Jsoup.parse(new File("/Users/gtackley/hack_regmem/mp-interests/mps-140922/yeo_tim.htm"), "UTF-8")

  val mp = RawParser(html)


  mp.categories foreach { c =>
    println(c.id + ": " + Categories.values(c.id))

    for (item <- c.lineItems) {
      println(s""" RawLineItem("${item.value}", "${item.indent}")""")
    }
    println()
  }

 }
