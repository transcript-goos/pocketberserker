package auctionsniper

import java.util.EventListener
import util.Announcer
import collection.mutable.ArrayBuffer

trait PortfolioListener extends EventListener {
  def sniperAdded(sniper: AuctionSniper)
}

class SniperPortfolio extends SniperCollector {

  private val listeners = Announcer.to[PortfolioListener]
  private val snipers = new ArrayBuffer[AuctionSniper]

  def +=(listener: PortfolioListener) {
    listeners += listener
  }

  def +=(sniper: AuctionSniper) {
    snipers += sniper
    listeners.announce().sniperAdded(sniper)
  }
}
