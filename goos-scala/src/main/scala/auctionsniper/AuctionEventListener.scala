package auctionsniper

trait AuctionEventListener {
  def auctionClosed()
  def currentPrice(price: Int, increment: Int)
}
