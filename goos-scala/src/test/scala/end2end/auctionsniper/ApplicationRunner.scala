package end2end.auctionsniper

object ApplicationRunner {
  val SNIPER_ID = "sniper"
  val SNIPER_PASSWORD = "sniper"
}

class ApplicationRunner {

  import auctionsniper.Main._
  import ApplicationRunner._

  private var driver : Option[AuctionSniperDriver] = None

  def startBiddingIn(auction: FakeAuctionServer) {
    val thread = new Thread("Test Application") {
      override def run() {
        try {
          main(Array(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.itemId))
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
}
