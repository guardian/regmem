import model.DataFile
import org.elasticsearch.action.index.{IndexRequest, IndexRequestBuilder}
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.node.NodeBuilder
import play.api.libs.json.Json

import scala.util.control.NonFatal


object IndexElasticsearch extends App {
  val indexName = "interests"

  val node = NodeBuilder.nodeBuilder().clusterName("mps-interests").node()
  val es = node.client()

  try {
    //if (es.admin().indices().prepareExists(indexName).get().isExists) {
      println("deleting existing index")
      es.admin().indices().prepareDelete(indexName).get()
      println("deleted")
    //}

    val indexSettings = ImmutableSettings.settingsBuilder()
      .put("index.number_of_shards", 1)
      .put("index.auto_expand_replicas", "0-1")
      .build()

    println("creating...")
    es.admin().indices().prepareCreate(indexName).setSettings(indexSettings).get()
    println("created")

    for (mp <- DataFile.all) try {
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

    } catch {
      case NonFatal(e) =>
        println(s"failed to index: ${mp.niceName}: $e")
    }
  } finally {
    node.close()
  }
}

