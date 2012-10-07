package auctionsniper

case class SniperSnapshot (
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState) {

  def bidding(newLastPrice: Int, newLastBid: Int) =
    copy(lastPrice = newLastPrice, lastBid = newLastBid, state = SniperState.BIDDING)

  def winning(newLastPrice: Int) = copy(lastPrice = newLastPrice, state = SniperState.WINNING)
}

object SniperSnapshot {
  def joining(itemId: String) =
    SniperSnapshot(itemId, 0, 0, SniperState.JOINNING)
}

