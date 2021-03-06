package auctionsniper

class AuctionSniper(private val item: Item, private val auction: Auction)
  extends AuctionEventListener {

  private var sniperListener: Option[SniperListener] = None
  private var snapshot = SniperSnapshot.joining(item.identifier)

  def auctionClosed() {
    snapshot = snapshot.closed()
    notifyChange()
  }

  def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
    priceSource match {
      case FromSniper() => snapshot = snapshot.winning(price)
      case FromOtherBidder() =>
        val bid = price + increment
        if (item.allowsBid(bid)) {
          auction.bid(price + increment)
          snapshot = snapshot.bidding(price, bid)
        } else {
          snapshot = snapshot.losing(price)
        }
    }
    notifyChange()
  }

  def notifyChange() {
    sniperListener.foreach(_.sniperStateChanged(snapshot))
  }

  def getSnapshot = snapshot

  def +=(listener: SniperListener) {
    sniperListener = Some(listener)
  }

  def auctionFailed() {
    snapshot = snapshot.failed()
    notifyChange()
  }
}

