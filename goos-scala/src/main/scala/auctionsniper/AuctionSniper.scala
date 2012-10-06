package auctionsniper

class AuctionSniper(private val auction: Auction, private val sniperListener: SniperListener)
  extends AuctionEventListener {

  private var isWinning = false

  def auctionClosed() {
    if (isWinning) {
      sniperListener.sniperWon()
    } else {
      sniperListener.sniperLost()
    }
  }
  def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
    isWinning = priceSource match {
      case FromSniper() => true
      case FromOtherBidder() => false
    }
    if (isWinning) {
      sniperListener.sniperWinning()
    } else {
        auction.bid(price + increment)
        sniperListener.sniperBidding()
    }
  }
}
