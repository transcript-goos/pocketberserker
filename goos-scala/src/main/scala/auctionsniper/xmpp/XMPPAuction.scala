package auctionsniper.xmpp

import auctionsniper.{PriceSource, AuctionEventListener, Auction}
import org.jivesoftware.smack.{XMPPException, XMPPConnection}
import auctionsniper.util.Announcer

object XMPPAuction {
  val ITEM_ID_AS_LOGIN = "auction-%s"
  val AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + XMPPAuctionHouse.AUCTION_RESOURCE
  val JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;"
  val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;"

  private def auctionId(itemId: String, connection: XMPPConnection) =
    AUCTION_ID_FORMAT.format(itemId, connection.getServiceName)
}

class XMPPAuction(connection: XMPPConnection, itemId: String,
  failureReporter: XMPPFailureReporter) extends Auction {

  import XMPPAuction._

  private val auctionEventListeners = Announcer.to[AuctionEventListener]
  private val translator = translatorFor(connection)
  private val chat = connection.getChatManager.createChat(auctionId(itemId, connection), translator)

  this += chatDisconnectorFor(translator)

  def bid(amount: Int) {
    sendMessage(BID_COMMAND_FORMAT.format(amount))
  }

  def join() {
    sendMessage(JOIN_COMMAND_FORMAT)
  }

  private def sendMessage(message: String) {
    try {
      chat.sendMessage(message)
    } catch {
      case e: XMPPException => e.printStackTrace()
    }
  }

  def +=(listener: AuctionEventListener) {
    auctionEventListeners += listener
  }

  private def translatorFor(connection: XMPPConnection) =
    new AuctionMessageTranslator(
      connection.getUser,
      auctionEventListeners.announce(),
      failureReporter)

  private def chatDisconnectorFor(translator: AuctionMessageTranslator) = {
    new AuctionEventListener {
      def auctionFailed() {
        chat.removeMessageListener(translator)
      }

      def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {}

      def auctionClosed() {}
    }
  }
}

