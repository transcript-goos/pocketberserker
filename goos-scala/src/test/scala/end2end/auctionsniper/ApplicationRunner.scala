package end2end.auctionsniper

import auctionsniper.ui.SnipersTableModel

object ApplicationRunner {
  val SNIPER_ID = "sniper"
  val SNIPER_PASSWORD = "sniper"
  val SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_HOSTNAME + "/Auction"

  def arguments(auctions: Seq[FakeAuctionServer]) = {
    Array(
      FakeAuctionServer.XMPP_HOSTNAME,
      SNIPER_ID,
      SNIPER_PASSWORD
    ) ++ auctions.map(_.itemId)
  }
}

class ApplicationRunner {

  import auctionsniper._
  import ui.MainWindow
  import ApplicationRunner._

  private var driver : Option[AuctionSniperDriver] = None

  def startBiddingIn(auctions: FakeAuctionServer*) {
    val thread = new Thread("Test Application") {
      override def run() {
        try {
          Main.main(arguments(auctions))
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
    thread.setDaemon(true)
    thread.start()

    val d = new AuctionSniperDriver(1000)
    d.hasTitle(MainWindow.APPLICATION_TITLE)
    d.hasColumnTitles()
    auctions.foreach{ (auction: FakeAuctionServer) =>
      d.showsSniperStatus(auction.itemId, 0, 0, SnipersTableModel.textFor(SniperState.JOINNING))
    }
    driver = Some(d)
  }

  def showsSniperHasLostAcution() {
    driver.foreach(_.showsSniperStatus(SnipersTableModel.textFor(SniperState.LOST)))
  }

  def stop() {
    driver.foreach(_.dispose())
  }

  def hasShownSniperIsBidding(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
    driver.foreach(
      _.showsSniperStatus(auction.itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING)))
  }

  def hasShownSniperIsWinning(auction: FakeAuctionServer, winningBid: Int) {
    driver.foreach(
      _.showsSniperStatus(auction.itemId, winningBid, winningBid, SnipersTableModel.textFor(SniperState.WINNING)))
  }

  def showsSniperHasWonAcution(auction: FakeAuctionServer, lastPrice: Int) {
    driver.foreach(
      _.showsSniperStatus(auction.itemId, lastPrice, lastPrice, SnipersTableModel.textFor(SniperState.WON)))
  }
}

