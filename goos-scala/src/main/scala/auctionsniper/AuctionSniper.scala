package auctionsniper

class AuctionSniper(private val auction: Auction, private val sniperListener: SniperListener)
  extends AuctionEventListener {

  def auctionClosed() {
    sniperListener.sniperLost()
  }
  def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {
    auction.bid(price + increment)
    sniperListener.sniperBidding()
  }
}
