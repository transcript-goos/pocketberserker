package auctionsniper

case class Item(identifier: String, stopPrice: Int) {
  def allowsBid(bid: Int) = bid <= stopPrice
}

