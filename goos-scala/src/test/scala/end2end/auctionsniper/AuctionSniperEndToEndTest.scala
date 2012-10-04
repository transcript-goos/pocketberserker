package end2end.auctionsniper

import org.specs2.matcher.JUnitMustMatchers
import org.junit.{After, Test}

class AuctionSniperEndToEndTest extends JUnitMustMatchers {

  private val auction = new FakeAuctionServer("item-54321")
  private val application = new ApplicationRunner()

  @Test
  def sniperJoinsAuctionUntilAuctionCloses() {
    auction.startSellingItem()
    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFromSniper()
    auction.announceClosed()
    application.showsSniperHasLostAcution()
  }

  @After
  def stopAuction() {
    auction.stop()
  }

  @After
  def stopApplication() {
    application.stop()
  }
}
