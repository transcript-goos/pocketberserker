package end2end.auctionsniper

import org.jivesoftware.smack.{MessageListener, Chat, ChatManagerListener, XMPPConnection}
import org.jivesoftware.smack.packet.Message
import java.util.concurrent.{TimeUnit, ArrayBlockingQueue}
import org.specs2.matcher.{AlwaysMatcher, JUnitMustMatchers, Matcher}
import auctionsniper.Main

object FakeAuctionServer {
  val ITEM_ID_AS_LOGIN = "auction-%s"
  val AUCTION_RESOURCE = "Auction"
  val XMPP_HOSTNAME = "localhost"
  private val AUCTION_PASSWORD = "auction"
}

class FakeAuctionServer(val itemId: String) extends JUnitMustMatchers {

  import FakeAuctionServer._

  private val connection = new XMPPConnection(XMPP_HOSTNAME)
  private var currentChat : Option[Chat] = None
  private val messageListener = new SingleMessageListener()

  def startSellingItem() {
    connection.connect()
    connection.login(ITEM_ID_AS_LOGIN.format(itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)
    connection.getChatManager.addChatListener(
      new ChatManagerListener() {
        def chatCreated(chat: Chat, createdLocally: Boolean) {
          currentChat = Some(chat)
          chat.addMessageListener(messageListener)
        }
      }
    )
  }

  def hasReceivedJoinRequestFromSniper() {
    messageListener.receivesAMessage(new AlwaysMatcher())
  }

  def announceClosed() {
    currentChat.foreach(_.sendMessage(new Message()))
  }

  def stop() {
    connection.disconnect()
  }

  def reportPrice(price: Int, increment: Int, bidder: String) {
    currentChat.foreach(_.sendMessage(
      "SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;"
        .format(price, increment, bidder)
    ))
  }

  def hasReceivedBid(bid: Int, sniperId: String) {
    currentChat.foreach(_.getParticipant must_== sniperId)
    messageListener.receivesAMessage(equalTo(
      "SOLVersion: 1.1; Command: BID; Price: %d;".format(bid)
    ))
  }

  class SingleMessageListener extends MessageListener {

    private val messages = new ArrayBlockingQueue[Message](1)

    def processMessage(chat: Chat, message: Message) {
      messages.add(message)
    }

    @SuppressWarnings(Array("unchecked"))
    def receivesAMessage[T >: String](messageMatcher: Matcher[T]) {
      val message =  messages.poll(5, TimeUnit.SECONDS)
      message must not beNull
      val body = message.getBody
      body must messageMatcher
    }
  }
}

