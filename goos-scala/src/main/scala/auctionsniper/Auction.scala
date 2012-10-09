package auctionsniper

trait Auction {
  def bid(amount: Int)
  def join()
  def +=(listener: AuctionEventListener)
}
