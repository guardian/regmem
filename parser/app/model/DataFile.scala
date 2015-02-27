package model

import java.io.File
import java.nio.file.{FileSystems, Path, Files}

import org.jsoup.Jsoup


class DataFile
(
  file: File
) {
  def niceName = file.getName.stripSuffix(".htm")

  lazy val doc = Jsoup.parse(file, "UTF-8")

  lazy val mainText = doc.select("#mainTextBlock")

  lazy val rawInfo = RawParser(doc)

  override def toString: String = file.getAbsolutePath
}

object DataFile {

  private val DIR = "data"

  import scala.collection.convert.wrapAll._
  lazy val all: Seq[DataFile] =
    for (file <- Files.newDirectoryStream(FileSystems.getDefault.getPath(DIR)).toSeq) yield {
      new DataFile(file.toFile)
    }

  def fromNiceName(niceName: String) = {
    val fileName = if (niceName.endsWith(".htm")) niceName else niceName + ".htm"
    new DataFile(new File(DIR, fileName))
  }
}
