package auctionsniper

class SniperLauncher(auctionHouse: AuctionHouse, collector: SniperCollector)
  extends UserRequestListener {

  def joinAuction(item: Item) {
    val auction = auctionHouse.auctionFor(item.identifier)
    val sniper = new AuctionSniper(item.identifier, auction)
    auction += sniper
    collector += sniper
    auction.join()
  }
}

