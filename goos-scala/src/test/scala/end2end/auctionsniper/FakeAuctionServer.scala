package end2end.auctionsniper

import org.jivesoftware.smack.{MessageListener, Chat, ChatManagerListener, XMPPConnection}
import org.jivesoftware.smack.packet.Message
import java.util.concurrent.{TimeUnit, ArrayBlockingQueue}
import org.specs2.matcher.JUnitMustMatchers

object FakeAuctionServer {
  val ITEM_ID_AS_LOGIN = "auction-%s"
  val AUCTION_RESOURCE = "Auction"
  val XMPP_HOSTNAME = "localhost"
  val AUCTION_PASSWORD = "auction"
}

class FakeAuctionServer(val itemId: String) extends JUnitMustMatchers {

  import FakeAuctionServer._

  private val connection = new XMPPConnection(XMPP_HOSTNAME)
  private var currentChat : Option[Chat] = None

  def startSellingItem() {
    connection.connect()
    connection.login(ITEM_ID_AS_LOGIN.format(itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)
    connection.getChatManager.addChatListener(
      new ChatManagerListener() {
        def chatCreated(chat: Chat, createdLocally: Boolean) {
          currentChat = Some(chat)
        }
      }
    )
  }
}
