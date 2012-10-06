package auctionsniper

import java.util.EventListener

trait SniperListener extends EventListener {
  def sniperLost() : Unit
  def sniperBidding(sniperState: SniperState): Unit
  def sniperWinning(): Unit
  def sniperWon(): Unit
}
