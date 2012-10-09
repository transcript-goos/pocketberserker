package auctionsniper

trait AuctionHouse {
  def auctionFor(itemId: String): Auction
}
