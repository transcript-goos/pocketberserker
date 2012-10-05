package auctionsniper

trait AuctionEventListener {
  def auctionClosed() : Unit
  def currentPrice(price: Int, increment: Int) : Unit
}
