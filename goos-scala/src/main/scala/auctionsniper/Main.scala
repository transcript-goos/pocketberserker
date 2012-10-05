package auctionsniper

import javax.swing.SwingUtilities
import org.jivesoftware.smack.{XMPPException, Chat, XMPPConnection}
import java.awt.event.{WindowEvent, WindowAdapter}

object Main {
  val BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;"

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

class Main extends SniperListener {

  import Main._
  import ui.MainWindow

  private var window : Option[MainWindow] = None

  @SuppressWarnings(Array("unused"))
  private var notToBeGCd : Option[Chat] = None

  startUserInterface()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable() {
      def run() {
        window = Some(new MainWindow())
      }
    })
  }

  private def joinAuction(connection: XMPPConnection, itemId: String) {

    disconnectWhenUICloses(connection)

    val chat = connection.getChatManager.createChat(auctionId(itemId, connection), null)
    notToBeGCd = Some(chat)

    val auction = new XMPPAuction(chat)
    chat.addMessageListener(
      new AuctionMessageTranslator(new AuctionSniper(auction, this)))
    auction.join()
  }

  def sniperLost() {
    SwingUtilities.invokeLater(new Runnable() {
      def run() {
        window.foreach(_.showStatus((MainWindow.STATUS_LOST)))
      }
    })
  }

  private def disconnectWhenUICloses(connection: XMPPConnection) {
    window.foreach(
      _.addWindowListener(new WindowAdapter() {
        override def windowClosed(e: WindowEvent) {
          connection.disconnect()
        }
      })
    )
  }

  def sniperBidding() {
    SwingUtilities.invokeLater(new Runnable {
      def run() {
        window.foreach(_.showStatus(MainWindow.STATUS_BIDDING))
      }
    })
  }

  class XMPPAuction(private val chat: Chat) extends Auction {

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
  }
}

