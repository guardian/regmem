package model

import java.io.File
import java.nio.file.{FileSystems, Path, Files}

import org.jsoup.Jsoup
import play.twirl.api.Html

import scala.util.Try


class DataFile
(
  file: File
) {
  def niceName = file.getName.stripSuffix(".htm")

  def categoryids = Try {
    Html(parsedCategories.map(c => s"""<span class="${c.cssClass}">${c.id}</span>""" ).mkString(", "))
  } getOrElse Html("")

  lazy val doc = Jsoup.parse(file, "UTF-8")

  lazy val mainText = doc.select("#mainTextBlock")

  lazy val rawInfo = RawParser(doc)

  lazy val parsedCategories = rawInfo.categories.map(CategoryParser.apply)

  lazy val nameAndConstituency = new NameConstituencyParser(rawInfo.name).NameConstituency.run().get

  def isValid = Try(parsedCategories).isSuccess

  def cssClass = if (isValid) "" else "text-danger"

  override def toString: String = file.getAbsolutePath
}

object DataFile {

  private val DIR = "data"

  import scala.collection.convert.wrapAll._
  lazy val all: Seq[DataFile] =
    for (file <- Files.newDirectoryStream(FileSystems.getDefault.getPath(DIR)).toSeq) yield {
      new DataFile(file.toFile)
    }

  lazy val topCapitalists = all.sortBy(d => - Try(d.rawInfo.totalCash).getOrElse(BigDecimal(0)))

  def fromNiceName(niceName: String) = {
    val fileName = if (niceName.endsWith(".htm")) niceName else niceName + ".htm"
    new DataFile(new File(DIR, fileName))
  }
}
