package auctionsniper

trait AuctionEventListener {
  def auctionClosed()
  def currentPrice(price: Int, increment: Int, priceSource: PriceSource)
}

trait PriceSource {}
case class FromSniper() extends PriceSource
case class FromOtherBidder() extends PriceSource

