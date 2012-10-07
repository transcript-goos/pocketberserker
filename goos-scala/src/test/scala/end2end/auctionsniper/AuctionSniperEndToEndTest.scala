package end2end.auctionsniper

import org.specs2.mutable.Specification
import org.specs2.mutable.After

class AuctionSniperEndToEndTest extends Specification {
  sequential

  trait after extends After {
    val auction = new FakeAuctionServer("item-54321")
    val application = new ApplicationRunner()
    def after {
      auction.stop()
      application.stop()
    }
  }

  "AuctionSniper" should {

    "join auction until auction closes" in new after {
      auction.startSellingItem()
      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auction.announceClosed()
      application.showsSniperHasLostAcution()
    }

    "make a higher bid but loses" in new after {
      auction.startSellingItem()

      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1000, 98, "other bidder")

      application.hasShownSniperIsBidding(1000, 1098)
      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

      auction.announceClosed()
      application.showsSniperHasLostAcution()
    }

    "win an auction by bidding higher" in new after {
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
  }
}

