package auctionsniper

class AuctionSniper(private val itemId: String, private val auction: Auction, private val sniperListener: SniperListener)
  extends AuctionEventListener {

  private var isWinning = false
  private var snapshot = SniperSnapshot.joining(itemId)

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
      snapshot = snapshot.winning(price)
    } else {
      val bid = price + increment
      auction.bid(price + increment)
      snapshot = snapshot.bidding(price, bid)
    }
    sniperListener.sniperStateChanged(snapshot)
  }
}
