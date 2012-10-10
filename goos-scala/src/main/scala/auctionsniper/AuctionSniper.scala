package auctionsniper

class AuctionSniper(private val itemId: String, private val auction: Auction)
  extends AuctionEventListener {

  private var sniperListener: Option[SniperListener] = None
  private var snapshot = SniperSnapshot.joining(itemId)

  def this(itemId: String, auction: Auction, sniperListener: SniperListener) {
    this(itemId, auction)
    this.sniperListener = Some(sniperListener)
  }

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
    sniperListener.foreach(_.sniperStateChanged(snapshot))
  }

  def getSnapshot = snapshot

  def +=(listener: SniperListener) {
    sniperListener = Some(listener)
  }
}

