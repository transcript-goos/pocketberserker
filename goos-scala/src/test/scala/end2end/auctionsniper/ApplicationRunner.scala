package end2end.auctionsniper

object ApplicationRunner {
  val SNIPER_ID = "sniper"
  val SNIPER_PASSWORD = "sniper"
  val SNIPER_XMPP_ID = SNIPER_ID + "@" + FakeAuctionServer.XMPP_HOSTNAME + "/Auction"
}

class ApplicationRunner {

  import auctionsniper._
  import MainWindow._
  import ApplicationRunner._

  private var driver : Option[AuctionSniperDriver] = None

  def startBiddingIn(auction: FakeAuctionServer) {
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

  def hasShownSniperIsBidding() {
    driver.foreach(_.showsSniperStatus(MainWindow.STATUS_BIDDING))
  }
}
