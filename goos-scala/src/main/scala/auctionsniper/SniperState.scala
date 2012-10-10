package auctionsniper

import com.objogate.exception.Defect

sealed class SniperState(val ordinal: Int) {
  def whenAuctionClosed : SniperState = {
    throw new Defect("Auction is already closed")
  }
}

object SniperState {

  object JOINNING extends SniperState(0) {
    override def whenAuctionClosed = LOST
  }
  object BIDDING extends SniperState(1) {
    override def whenAuctionClosed = LOST
  }
  object WINNING extends SniperState(2) {
    override def whenAuctionClosed = WON
  }
  object LOSING extends SniperState(3) {
    override def whenAuctionClosed = LOST
  }
  object LOST extends SniperState(4)
  object WON extends SniperState(5)
  object FAILED extends SniperState(6)
}

