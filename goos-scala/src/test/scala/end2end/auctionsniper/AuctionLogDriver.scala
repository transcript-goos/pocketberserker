package end2end.auctionsniper

import java.io.File
import org.specs2.matcher.{MustMatchers, Matcher}
import org.apache.commons.io.FileUtils
import java.util.logging.LogManager
import auctionsniper.xmpp.XMPPAuctionHouse

class AuctionLogDriver extends MustMatchers {

  private val logFile = new File(XMPPAuctionHouse.LOG_FILE_NAME)

  def hasEntry(matcher: Matcher[String]) {
    FileUtils.readFileToString(logFile) must matcher
  }

  def clearLog() {
    logFile.delete()
    LogManager.getLogManager.reset()
  }
}
