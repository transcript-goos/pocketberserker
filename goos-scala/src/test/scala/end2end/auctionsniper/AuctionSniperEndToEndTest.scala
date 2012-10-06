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
    auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
    auction.announceClosed()
    application.showsSniperHasLostAcution()
  }

  @Test
  def sniperMakesAHigherBidButLoses() {
    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

    auction.reportPrice(1000, 98, "other bidder")

    application.hasShownSniperIsBidding(1000, 1098)
    auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

    auction.announceClosed()
    application.showsSniperHasLostAcution()
  }

  @Test
  def sniperWinsAnAuctionByBiddingHigher() {
    auction.startSellingItem()

    application.startBiddingIn(auction)
    auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

    auction.reportPrice(1000, 98, "other bidder")
    application.hasShownSniperIsBidding(1000, 1098)

    auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

    auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID)
    application.hasShownSniperIsWinning(1098)

    auction.announceClosed()
    application.showsSniperHasWonAcution(1098)
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
