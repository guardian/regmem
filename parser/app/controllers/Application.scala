package controllers

import model.DataFile
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.node.NodeBuilder
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.util.Try
import scala.util.control.NonFatal

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

  def reindex() = Action {
    //if (es.admin().indices().prepareExists(indexName).get().isExists) {

    try {
      println("deleting existing index")
      es.admin().indices().prepareDelete(indexName).get()
      println("deleted")
    } catch {
      case NonFatal(ex) =>
        println("ignoring exception: " + ex)
    }

    val indexSettings = ImmutableSettings.settingsBuilder()
      .put("index.number_of_shards", 1)
      .put("index.auto_expand_replicas", "0-1")
      .build()

    println("creating...")
    es.admin().indices().prepareCreate(indexName).setSettings(indexSettings).get()
    println("created")

    val counts = for (mp <- DataFile.all) yield try {
      println(s"indexing ${mp.rawInfo.name}")

      val jsonBits = for {
        category <- mp.parsedCategories
        item <- category.lineItems
      } yield {
        Json.obj(
          "mp" -> mp.rawInfo.name,
          "categoryId" -> category.id,
          "categoryName" -> category.name,
          "description" -> item.description
        ).toString()
      }

      if (jsonBits.nonEmpty) {
        val bulkReq = es.prepareBulk()

        for (json <- jsonBits) bulkReq.add(new IndexRequest(indexName, "item").source(json))

        bulkReq.get()
      }

      jsonBits.size
    } catch {
      case NonFatal(e) =>
        println(s"${mp.niceName} failed: " + e)
        0
    }

    Ok(s"${counts.size} indexed")
  }
}
