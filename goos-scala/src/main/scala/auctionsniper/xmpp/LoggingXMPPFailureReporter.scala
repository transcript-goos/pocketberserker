package auctionsniper.xmpp

import java.util.logging.Logger

class LoggingXMPPFailureReporter(val logger: Logger) extends XMPPFailureReporter {
  def cannotTranslateMessage(auctionId: String, failedMessage: String, exception: Exception) {
    logger.severe("<" + auctionId + "> "
      + "Could not translate message \"" + failedMessage + "\" "
      + "because \"" + exception + "\"")
  }
}

