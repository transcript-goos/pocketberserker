package auctionsniper

class SniperLauncher(auctionHouse: AuctionHouse, collector: SniperCollector)
  extends UserRequestListener {

  def joinAuction(itemId: String) {
    val auction = auctionHouse.auctionFor(itemId)
    val sniper = new AuctionSniper(itemId, auction)
    auction += sniper
    collector += sniper
    auction.join()
  }
}

