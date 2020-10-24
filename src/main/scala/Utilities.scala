import java.util

import com.google.gson.JsonParser

import scala.collection.JavaConverters.asScalaBuffer
import scala.collection.mutable

object Utilities {
  /** Makes sure only ERROR messages get logged to avoid log spam. */
  def setupLogging() {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

  /** Unused, this can parse the response of popular articles from NY Times API
      But that did not fit well with streaming application due to window of 1 day*/
  def parseNYTimesJSON(string: String): mutable.Buffer[String] = {
    val result  = new util.ArrayList[String]
    val parser = new JsonParser()
    val jsonObject = parser.parse(string).getAsJsonObject
    val results = jsonObject.get("results").getAsJsonArray
    results.forEach( news => {
      val newsJson = news.getAsJsonObject
      newsJson.get("adx_keywords").getAsString.split(";").filter(!_.isEmpty).foreach(keyword => result.add(keyword))
    })
    asScalaBuffer(result)
  }

  def parseCurrentsAPIJSON(string: String): mutable.Buffer[String] = {
    val result  = new util.ArrayList[String]
    val parser = new JsonParser()
    val jsonObject = parser.parse(string).getAsJsonObject
    val results = jsonObject.get("news").getAsJsonArray
    results.forEach( news => {
      val newsJson = news.getAsJsonObject
      newsJson.get("description").getAsString.toLowerCase().split("\\W+").filter( it => !it.isEmpty && it.length > 4 && !List("these","there","their","where","coming","going","https").contains(it)).foreach(keyword => result.add(keyword))
    })
    asScalaBuffer(result)
  }

}
