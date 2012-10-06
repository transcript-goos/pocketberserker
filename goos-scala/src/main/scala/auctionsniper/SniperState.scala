package auctionsniper

case class SniperState(
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int)
