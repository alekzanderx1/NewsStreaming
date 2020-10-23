import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

class APIReceiver(api: String, key:String) extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging
{
  def onStart() {
    // Start the thread that
    new Thread("API Receiver Thread") {
      override def run() {
        //receive()
      }
    }.start()
  }

  def onStop() {
    // There is nothing much to do as the thread calling receive()
    // is designed to stop by itself if isStopped() returns false
  }
}
