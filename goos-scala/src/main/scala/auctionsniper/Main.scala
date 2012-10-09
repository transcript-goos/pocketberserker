package auctionsniper

import javax.swing.SwingUtilities
import org.jivesoftware.smack.XMPPConnection
import java.awt.event.{WindowEvent, WindowAdapter}
import ui.SnipersTableModel
import xmpp.XMPPAuction

object Main {
  private val ARG_HOSTNAME = 0
  private val ARG_USERNAME = 1
  private val ARG_PASSWORD = 2

  def main(args: Array[String]) {
    val main = new Main()
    val connection = this.connection(args(ARG_HOSTNAME), args(ARG_USERNAME), args(ARG_PASSWORD))
    main.disconnectWhenUICloses(connection)
    main.addUserRequestListenerFor(connection)
  }

  private def connection(hostname: String, username: String, password: String) = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, XMPPAuction.AUCTION_RESOURCE)
    connection
  }
}

class Main {

  import ui.MainWindow
  import scala.collection.mutable.ListBuffer

  private var window : Option[MainWindow] = None
  private val snipers = new SnipersTableModel

  private val notToBeGCd = new ListBuffer[Auction]

  startUserInterface()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable {
      def run() {
        window = Some(new MainWindow(snipers))
      }
    })
  }

  private def disconnectWhenUICloses(connection: XMPPConnection) {
    window.foreach(
      _.addWindowListener(new WindowAdapter {
        override def windowClosed(e: WindowEvent) {
          connection.disconnect()
        }
      })
    )
  }

  private def addUserRequestListenerFor(connection: XMPPConnection) {
    window.foreach(
      _ += new UserRequestListener {
        def joinAuction(itemId: String) {
          snipers += SniperSnapshot.joining(itemId)
          val auction = new XMPPAuction(connection, itemId)
          notToBeGCd += auction
          auction += new AuctionSniper(
            itemId, auction, new SwingThreadSniperListener(snipers))
          auction.join()
        }
      }
    )
  }

  class SwingThreadSniperListener(val delegate: SniperListener) extends SniperListener {

    def sniperStateChanged(snapshot: SniperSnapshot) {
      SwingUtilities.invokeLater(new Runnable {
        def run() {
          delegate.sniperStateChanged(snapshot)
        }
      })
    }
  }
}

