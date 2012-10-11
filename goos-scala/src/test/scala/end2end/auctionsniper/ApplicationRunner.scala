package end2end.auctionsniper

import auctionsniper.ui.SnipersTableModel
import org.specs2.matcher.Matchers

object ApplicationRunner {
  val SNIPER_ID = "sniper"
  val SNIPER_PASSWORD = "sniper"
  val SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_HOSTNAME + "/Auction"

  def arguments(auctions: FakeAuctionServer*) = {
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
  private val logDriver = new AuctionLogDriver()

  def startBiddingIn(auctions: FakeAuctionServer*) {
    startSniper()
    auctions.foreach(openBiddingFor(_, Int.MaxValue))
  }

  private def startSniper() {
    logDriver.clearLog()
    val thread = new Thread("Test Application") {
      override def run() {
        try {
          Main.main(arguments())
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
    driver = Some(d)
  }

  def showsSniperHasLostAcution(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
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

  def hasShownSniperIsLosing(auction: FakeAuctionServer, lastPrice: Int, lastBid: Int) {
    driver.foreach(
      _.showsSniperStatus(auction.itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOSING)))
  }

  def startBiddingWithStopPrice(auction: FakeAuctionServer, stopPrice: Int) {
    startSniper()
    openBiddingFor(auction, stopPrice)
  }

  private def openBiddingFor(auction: FakeAuctionServer, stopPrice: Int) {
    val itemId = auction.itemId
    driver.foreach{ (d: AuctionSniperDriver) =>
      d.startBiddingFor(itemId, stopPrice)
      d.showsSniperStatus(itemId, 0, 0, SnipersTableModel.textFor(SniperState.JOINNING))
    }
  }

  def showsSniperHasFailed(auction: FakeAuctionServer) {
    driver.foreach(
      _.showsSniperStatus(auction.itemId, 0, 0, SnipersTableModel.textFor(SniperState.FAILED)))
  }

  def reportsInvalidMessage(auction: FakeAuctionServer, message: String) {
    logDriver.hasEntry(Matchers.contain(message))
  }
}

