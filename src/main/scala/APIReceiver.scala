import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class APIReceiver(url: String) extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging
{
  def onStart() {
    // Start the thread which calls the receive method to fetch and store data
    new Thread("API Receiver Thread") {
      override def run() { receive() }
    }.start()
  }

  //This methods is used to fetch and store the data using the provided API and security key
  def receive() {
    try {
      val get = new HttpGet(url)
      val httpclient = new DefaultHttpClient
      //TODO: add a method to check if loop can be exit-ed
      while(true) {
        val response = httpclient.execute(get)
        val br = new BufferedReader(new InputStreamReader(response.getEntity.getContent))
        store(br.readLine)
        Thread.sleep(6000)
      }
    } catch {
      case e: ClientProtocolException =>
        e.printStackTrace()
      case e: IOException =>
        e.printStackTrace()
    }
  }

  def onStop() {
    // There is nothing much to do as the thread calling receive()
    // is designed to stop by itself if isStopped() returns false
  }
}
