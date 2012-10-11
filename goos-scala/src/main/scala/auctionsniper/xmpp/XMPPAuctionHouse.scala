package auctionsniper.xmpp

import auctionsniper.AuctionHouse
import org.jivesoftware.smack.XMPPConnection
import java.util.logging.{Handler, SimpleFormatter, FileHandler, Logger}
import org.apache.commons.io.FilenameUtils

object XMPPAuctionHouse {
  val AUCTION_RESOURCE = "Auction"
  val LOG_FILE_NAME = "auction-sniper.log"
  private val LOGGER_NAME = "auction-sniper"

  def connect(hostname: String, username: String, password: String) = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    new XMPPAuctionHouse(connection)
  }
}

class XMPPAuctionHouse(connection: XMPPConnection) extends AuctionHouse {

  import XMPPAuctionHouse._

  private val failureReporter = new LoggingXMPPFailureReporter(makeLogger())

  def auctionFor(itemId: String) = new XMPPAuction(connection, itemId, failureReporter)

  def disconnect() {
    connection.disconnect()
  }

  private def makeLogger() = {
    val logger = Logger.getLogger(LOGGER_NAME)
    logger.setUseParentHandlers(false)
    logger.addHandler(simpleFileHandler())
    logger
  }

  private def simpleFileHandler() = {
    try {
      val handler = new FileHandler(LOG_FILE_NAME)
      handler.setFormatter(new SimpleFormatter)
      handler
    } catch {
      case e: Exception =>
        throw new XMPPAuctionException("Could not create logger FileHandler "
          + FilenameUtils.getFullPath(LOG_FILE_NAME), e)
    }
  }
}

