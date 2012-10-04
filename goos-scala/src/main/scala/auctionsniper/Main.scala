package auctionsniper

import javax.swing.SwingUtilities
import org.jivesoftware.smack.{Chat, MessageListener, XMPPConnection}
import org.jivesoftware.smack.packet.Message

object Main {

  val STATUS_JOINING = "Joining"
  val STATUS_LOST = "Lost"
  private val ARG_HOSTNAME = 0
  private val ARG_USERNAME = 1
  private val ARG_PASSWORD = 2
  private val ARG_ITEM_ID  = 3
  private val AUCTION_RESOURCE = "Auction"
  private val ITEM_ID_AS_LOGIN = "auction-%s"
  private val AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE

  def main(args: Array[String]) {
    val main = new Main()
    val connection = connectTo(args(ARG_HOSTNAME), args(ARG_USERNAME), args(ARG_PASSWORD))
    val chat = connection.getChatManager().createChat(auctionId(args(ARG_ITEM_ID), connection),
      new MessageListener() {
        def processMessage(aChat: Chat, message: Message) {
        }
      })
    chat.sendMessage(new Message())
  }

  private def connectTo(hostname: String, username: String, password: String) = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    connection
  }

  private def auctionId(itemId: String, connection: XMPPConnection) = {
    AUCTION_ID_FORMAT.format(itemId, connection.getServiceName)
  }
}

class Main {

  private var ui : Option[MainWindow] = None

  startUserInterface()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable() {
      def run() {
        ui = Some(new MainWindow())
      }
    })
  }
}

