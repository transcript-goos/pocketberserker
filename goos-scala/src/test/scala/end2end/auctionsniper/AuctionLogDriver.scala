package end2end.auctionsniper

import java.io.File
import org.specs2.matcher.{MustMatchers, Matcher}
import org.apache.commons.io.FileUtils
import java.util.logging.LogManager

object AuctionLogDriver extends MustMatchers {
  val LOG_FILE_NAME = "auction-sniper.log"
}

class AuctionLogDriver {

  import AuctionLogDriver._

  private val logFile = new File(LOG_FILE_NAME)

  def hasEntry(matcher: Matcher[String]) {
    FileUtils.readFileToString(logFile) must matcher
  }


  def clearLog() {
    logFile.delete()
    LogManager.getLogManager.reset()
  }
}
