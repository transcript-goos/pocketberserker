package auctionsniper

import javax.swing.SwingUtilities

class SniperLauncher(auctionHouse: AuctionHouse, collector: SniperCollector)
  extends UserRequestListener {

  def joinAuction(itemId: String) {
    val auction = auctionHouse.auctionFor(itemId)
    val sniper = new AuctionSniper(itemId, auction)
    auction += sniper
    collector += sniper
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
