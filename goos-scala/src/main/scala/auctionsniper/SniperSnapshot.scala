package auctionsniper

case class SniperSnapshot (
  itemId: String,
  lastPrice: Int,
  lastBid: Int,
  state: SniperState) {

  def bidding(newLastPrice: Int, newLastBid: Int) =
    copy(lastPrice = newLastPrice, lastBid = newLastBid, state = SniperState.BIDDING)

  def winning(newLastPrice: Int) = copy(lastPrice = newLastPrice, state = SniperState.WINNING)

  def closed() = copy(state = state.whenAuctionClosed)
}

object SniperSnapshot {
  def joining(itemId: String) =
    SniperSnapshot(itemId, 0, 0, SniperState.JOINNING)
}

