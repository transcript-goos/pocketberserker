package auctionsniper

import javax.swing.SwingUtilities
import org.jivesoftware.smack.{Chat, MessageListener, XMPPConnection}
import org.jivesoftware.smack.packet.Message
import java.awt.event.{WindowEvent, WindowAdapter}

object Main {
  val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Bid; Price: %d;"

  private val ARG_HOSTNAME = 0
  private val ARG_USERNAME = 1
  private val ARG_PASSWORD = 2
  private val ARG_ITEM_ID  = 3
  private val AUCTION_RESOURCE = "Auction"
  private val ITEM_ID_AS_LOGIN = "auction-%s"
  private val AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE
  val JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;"

  def main(args: Array[String]) {
    val main = new Main()
    main.joinAuction(connection(args(ARG_HOSTNAME), args(ARG_USERNAME), args(ARG_PASSWORD)), args(ARG_ITEM_ID))
  }

  private def connection(hostname: String, username: String, password: String) = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    connection
  }

  private def auctionId(itemId: String, connection: XMPPConnection) =
    AUCTION_ID_FORMAT.format(itemId, connection.getServiceName)
}

class Main extends AuctionEventListener {

  import Main._

  private var ui : Option[MainWindow] = None

  @SuppressWarnings(Array("unused"))
  private var notToBeGCd : Option[Chat] = None

  startUserInterface()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable() {
      def run() {
        ui = Some(new MainWindow())
      }
    })
  }

  private def joinAuction(connection: XMPPConnection, itemId: String) {
    disconnectWhenUICloses(connection)
    val chat = connection.getChatManager.createChat(auctionId(itemId, connection),
      new AuctionMessageTranslator(this))
    notToBeGCd = Some(chat)
    chat.sendMessage(JOIN_COMMAND_FORMAT)
  }

  def auctionClosed() {
    SwingUtilities.invokeLater(new Runnable() {
      def run() {
        ui.foreach(_.showStatus((MainWindow.STATUS_LOST)))
      }
    })
  }

  private def disconnectWhenUICloses(connection: XMPPConnection) {
    ui.foreach(
      _.addWindowListener(new WindowAdapter() {
        override def windowClosed(e: WindowEvent) {
          connection.disconnect()
        }
      })
    )
  }

  def currentPrice(price: Int, increment: Int) {}
}

