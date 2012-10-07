package auctionsniper

import com.objogate.exception.Defect

case class SniperSnapshot (
  val itemId: String,
  val lastPrice: Int,
  val lastBid: Int,
  val state: SniperState) {

  def bidding(newLastPrice: Int, newLastBid: Int) =
    copy(lastPrice = newLastPrice, lastBid = newLastBid, state = SniperState.BIDDING)

  def winning(newLastPrice: Int) = copy(lastPrice = newLastPrice, state = SniperState.WINNING)

  def closed() = {
    state match {
      case SniperState.WINNING => copy(state = SniperState.WON)
      case SniperState.WON | SniperState.LOST => throw new Defect("Auction is already closed")
      case _ =>copy(state = SniperState.LOST)
    }
  }
}

object SniperSnapshot {
  def joining(itemId: String) =
    SniperSnapshot(itemId, 0, 0, SniperState.JOINNING)
}

