package auctionsniper

class AuctionSniper(private val itemId: String, private val auction: Auction, private val sniperListener: SniperListener)
  extends AuctionEventListener {

  private var snapshot = SniperSnapshot.joining(itemId)

  def auctionClosed() {
    snapshot = snapshot.closed()
    notifyChange()
  }

  def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
    priceSource match {
      case FromSniper() => snapshot = snapshot.winning(price)
      case FromOtherBidder() =>
        val bid = price + increment
        auction.bid(price + increment)
        snapshot = snapshot.bidding(price, bid)
    }
    notifyChange()
  }

  def notifyChange() {
    sniperListener.sniperStateChanged(snapshot)
  }
}

