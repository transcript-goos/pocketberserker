package auctionsniper

import ui.SnipersTableModel
import collection.mutable.ArrayBuffer
import javax.swing.SwingUtilities

class SniperLauncher(auctionHouse: AuctionHouse, snipers: SnipersTableModel)
  extends UserRequestListener {

  private val notToBeGCd = new ArrayBuffer[Auction]

  def joinAuction(itemId: String) {
    snipers += SniperSnapshot.joining(itemId)
    val auction = auctionHouse.auctionFor(itemId)
    notToBeGCd += auction
    val sniper = new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers))
    auction += sniper
    auction.join()
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
