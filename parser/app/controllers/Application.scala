package controllers

import model.DataFile
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.node.NodeBuilder
import play.api.mvc.{Action, Controller}

import scala.util.Try

object Application extends Controller {

  val indexName = "interests"

  val node = NodeBuilder.nodeBuilder().clusterName("mps-interests").node()
  val es = node.client()

  def index(category: Option[Int]) = Action { r =>
    Ok(views.html.index(filteredList(category), None, category, r.getQueryString("debug").isDefined))
  }

  def filteredList(category: Option[Int])= category.map(c =>
    model.DataFile.all.filter(d => Try(d.rawInfo.categories.map(_.id).contains(c)).getOrElse(false)
    )).
    getOrElse(model.DataFile.all)

  def showMp(mpName: String, category: Option[Int]) = Action { r =>
    Ok(views.html.index(filteredList(category), Some(DataFile.fromNiceName(mpName)), category, r.getQueryString("debug").isDefined))
  }

  def search(q: String) = Action {
    val results = es.prepareSearch(indexName)
      .setQuery(QueryBuilders.queryString(q))
      .addFields("mp", "categoryName", "description")
      .setSize(500)
      .get()

    Ok(views.html.search(results.getHits))

  }
}
