package auctionsniper.xmpp

import auctionsniper.AuctionHouse
import org.jivesoftware.smack.XMPPConnection

object XMPPAuctionHouse {
  val AUCTION_RESOURCE = "Auction"

  def connect(hostname: String, username: String, password: String) = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    new XMPPAuctionHouse(connection)
  }
}

class XMPPAuctionHouse(connection: XMPPConnection) extends AuctionHouse {

  def auctionFor(itemId: String) = new XMPPAuction(connection, itemId)

  def disconnect() {
    connection.disconnect()
  }
}

