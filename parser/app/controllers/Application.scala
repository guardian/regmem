package controllers

import model.DataFile
import play.api.mvc.{Action, Controller}

import scala.util.Try

object Application extends Controller {

  def index(category: Option[Int]) = Action {
    Ok(views.html.index(filteredList(category), None, category))
  }

  def filteredList(category: Option[Int])= category.map(c =>
    model.DataFile.all.filter(d => Try(d.rawInfo.categories.map(_.id).contains(c)).getOrElse(false)
    )).
    getOrElse(model.DataFile.all)

  def showMp(mpName: String, category: Option[Int]) = Action {
    Ok(views.html.index(filteredList(category), Some(DataFile.fromNiceName(mpName)), category))
  }
}
