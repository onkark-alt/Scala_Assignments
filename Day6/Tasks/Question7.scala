// DownloadTask.scala
class DownloadTask(val fileName: String, val downloadSpeed: Int) extends Thread {
  override def run(): Unit = {
    for (percent <- 10 to 100 by 10) {
      Thread.sleep(downloadSpeed)  // simulate download time per 10%
      println(s"$fileName: $percent% downloaded")
    }
    println(s"$fileName download completed!")
  }
}

object DownloadSimulator extends App {
  // Create multiple download tasks with different speeds (in milliseconds)
  val fileA = new DownloadTask("FileA.zip", 500)
  val fileB = new DownloadTask("FileB.mp4", 700)
  val fileC = new DownloadTask("FileC.pdf", 400)

  // Start all downloads concurrently
  fileA.start()
  fileB.start()
  fileC.start()

  // Optional: Wait for all downloads to complete before exiting
  fileA.join()
  fileB.join()
  fileC.join()

  println("All downloads completed!")
}
