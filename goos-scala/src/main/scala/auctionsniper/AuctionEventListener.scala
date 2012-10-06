package auctionsniper

trait AuctionEventListener {
  def auctionClosed() : Unit
  def currentPrice(price: Int, increment: Int, priceSource: PriceSource) : Unit
}

trait PriceSource {}
case class FromSniper() extends PriceSource
case class FromOtherBidder() extends PriceSource

