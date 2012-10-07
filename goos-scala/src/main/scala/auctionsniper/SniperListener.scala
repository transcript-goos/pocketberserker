package auctionsniper

import java.util.EventListener

trait SniperListener extends EventListener {
  def sniperLost() : Unit
  def sniperStateChanged(snapshot: SniperSnapshot): Unit
  def sniperWon(): Unit
}
