import org.apache.spark.streaming.{Seconds, StreamingContext}
import Utilities._
import org.apache.spark.SparkConf

object NewsStreaming {

  val defaultCheckpointDir = "C:/checkpoint/";
  val defaultBatchSize = "6"

  def main(args: Array[String]): Unit = {

    var finalApiUrl = args(0)

    //Very specific to currentsapi, where key is specified as apiKey in query parameters
    if(args(1) != null)
      finalApiUrl = finalApiUrl + "&apiKey=" + args(1)

    //Get existing streaming context from checkpoint or create a new one as specified in
    //getStreamingContext method
    val ssc = StreamingContext.getOrCreate(defaultCheckpointDir, getStreamingContext)

    setupLogging()

    //Initialize the API receiver
    val customReceiverStream = ssc.receiverStream(new APIReceiver(finalApiUrl))

    //parse JSON to get keywords from the news description as DStream of String
    val keywords = customReceiverStream.flatMap(it => parseCurrentsAPIJSON(it))

    //compute map of keyword to the count of number of times it has been used in the last 5 minutes(windowSize)
    //this is computed every 12th second(slideDuration) as APIReceiver polls every 12th second
    val keywordsByCount = keywords.map(x => (x,1)).reduceByKeyAndWindow(_ + _, _ - _, Seconds(300), Seconds(12))

    //Sort keywords in descending order by count
    val sortedResults = keywordsByCount.transform(rdd => rdd.sortBy(x => x._2, false))

    //print the top 20 keywords in the last 5 minutes
    sortedResults.map(_._1).print(20)

    ssc.start()
    ssc.awaitTermination()
  }

  def getStreamingContext() : StreamingContext = {
    val sc = new SparkConf().setAppName("NewsStreaming")
    val ssc =  new StreamingContext(sc, Seconds(6))
    ssc.checkpoint(defaultCheckpointDir)
    ssc
  }
}
