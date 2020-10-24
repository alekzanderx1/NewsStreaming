import org.apache.spark.streaming.{Seconds, StreamingContext}
import Utilities._
import com.google.gson.JsonParser

import scala.collection.mutable

object NewsStreaming {

  def main(args: Array[String]): Unit = {

    if (args.length < 1) {
      System.err.println("Usage:<api_url>")
      System.exit(1)
    }
    val ssc = new StreamingContext("local[*]", "NewsStreaming", Seconds(6))
    setupLogging()

    val customReceiverStream = ssc.receiverStream(new APIReceiver(args(0)))

    val keywords = customReceiverStream.flatMap(it => parseJSON(it))

    val keywordsByCount = keywords.map(x => (x,1)).reduceByKeyAndWindow(_ + _, _ - _, Seconds(300), Seconds(6))

    val sortedResults = keywordsByCount.transform(rdd => rdd.sortBy(x => x._2, false))

    sortedResults.print()

    //Todo: Make configurable
    ssc.checkpoint("C:/checkpoint/")
    ssc.start()
    ssc.awaitTermination()

  }

  def parseJSON(string: String): Stream[String] = {
    System.out.println("JSON string to parse is: " + string)
    val result  = mutable.HashSet[String]()
    val parser = new JsonParser()
    val jsonObject = parser.parse(string).getAsJsonObject
    val results = jsonObject.get("results").getAsJsonArray
    results.forEach( news => {
      val newsJson = news.getAsJsonObject
      newsJson.get("adx_keywords").getAsString.split(";").filter(!_.isEmpty).foreach(keyword => result.add(keyword))
    })
    result.toStream
  }
}
