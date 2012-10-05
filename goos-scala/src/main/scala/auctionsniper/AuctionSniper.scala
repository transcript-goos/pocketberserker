package auctionsniper

class AuctionSniper(private val sniperListener: SniperListener) extends AuctionEventListener {

  def auctionClosed() {
    sniperListener.sniperLost()
  }
  def currentPrice(price: Int, increment: Int) {}
}
