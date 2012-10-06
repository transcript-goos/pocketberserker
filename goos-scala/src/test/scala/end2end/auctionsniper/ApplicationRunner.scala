package end2end.auctionsniper

object ApplicationRunner {
  val SNIPER_ID = "sniper"
  val SNIPER_PASSWORD = "sniper"
  val SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_HOSTNAME + "/Auction"
}

class ApplicationRunner {

  import auctionsniper._
  import ui.MainWindow
  import ui.MainWindow._
  import ApplicationRunner._

  private var driver : Option[AuctionSniperDriver] = None
  private var itemId : Option[String] = None

  def startBiddingIn(auction: FakeAuctionServer) {
    itemId = Some(auction.itemId)
    val thread = new Thread("Test Application") {
      override def run() {
        try {
          Main.main(Array(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.itemId))
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
    thread.setDaemon(true)
    thread.start()
    val d = new AuctionSniperDriver(1000)
    d.showsSniperStatus(STATUS_JOINING)
    driver = Some(d)
  }

  def showsSniperHasLostAcution() {
    driver.foreach(_.showsSniperStatus(STATUS_LOST))
  }

  def stop() {
    driver.foreach(_.dispose())
  }

  def hasShownSniperIsBidding(lastPrice: Int, lastBid: Int) {
    for(d <- driver; id <- itemId)
      d.showsSniperStatus(id, lastPrice, lastBid, MainWindow.STATUS_BIDDING)
  }

  def hasShownSniperIsWinning(winningBid: Int) {
    for(d <- driver; id <- itemId)
      d.showsSniperStatus(id, winningBid, winningBid, MainWindow.STATUS_WINNING)
  }

  def showsSniperHasWonAcution(lastPrice: Int) {
    for(d <- driver; id <- itemId)
      d.showsSniperStatus(id, lastPrice, lastPrice, MainWindow.STATUS_WON)
  }
}

