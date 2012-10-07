package auctionsniper

sealed class SniperState(val ordinal: Int) {}

object SniperState {
  object JOINNING extends SniperState(0)
  object BIDDING extends SniperState(1)
  object WINNING extends SniperState(2)
  object LOST extends SniperState(3)
  object WON extends SniperState(4)
}

