package controllers

import model.DataFile
import play.api.mvc.{Action, Controller}

object Application extends Controller {

  def index() = Action {
    Ok(views.html.index(None))
  }

  def showMp(mpName: String) = Action {
    Ok(views.html.index(Some(DataFile.fromNiceName(mpName))))
  }
}
