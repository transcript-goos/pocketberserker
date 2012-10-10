package auctionsniper

import java.util.EventListener

class SniperPortfolio {
  trait PortfolioListener extends EventListener {
    def sniperAdded(sniper: AuctionSniper)
  }
}
