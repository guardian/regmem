package regmem

import scala.xml.XML
import regmem.model._

object Main extends App {
   println("hello")

   val elem = XML.loadFile("regmem2015-02-09.xml")

   for (member <- (elem \ "regmem") map (e => new Member(e))) {
     println(member.name)

     for (category <- member.categories) {
       println("  " + category)

       for (record <- category.records) {
         println("    RECORD->")
         println(record)
         println("    <-RECORD")
       }
     }
   }

   //members.foreach(m => println(m))

 //  val categories = members.flatMap(_.categories.map(c => s"""${c.categoryType} -> "${c.name}"""")).distinct.sorted
 //
 //  categories.foreach(println)

 //  println((elem \ "regmem" \ "@membername").size)
 //
 //  val childNames = elem.child.map(_.label).distinct
 //
 //  println(childNames)
 }
