package end2end.auctionsniper

import org.specs2.mutable.Specification
import org.specs2.mutable.After

class AuctionSniperEndToEndTest extends Specification {
  sequential

  trait after extends After {
    val auction = new FakeAuctionServer("item-54321")
    val auction2 = new FakeAuctionServer("item-65432")
    val application = new ApplicationRunner()

    def after {
      auction.stop()
      auction2.stop()
      application.stop()
    }

    def waitForAnotherAuctionEvent() {
      auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auction2.reportPrice(600, 6, "other bidder")
      application.hasShownSniperIsBidding(auction2, 600, 606)
    }
  }

  "AuctionSniper" should {

    "join auction until auction closes" in new after {
      auction.startSellingItem()
      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auction.announceClosed()
      application.showsSniperHasLostAcution(auction, 0, 0)
    }

    "make a higher bid but loses" in new after {
      auction.startSellingItem()

      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1000, 98, "other bidder")

      application.hasShownSniperIsBidding(auction, 1000, 1098)
      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

      auction.announceClosed()
      application.showsSniperHasLostAcution(auction, 1098, 1000)
    }

    "win an auction by bidding higher" in new after {
      auction.startSellingItem()

      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1000, 98, "other bidder")
      application.hasShownSniperIsBidding(auction, 1000, 1098)

      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID)
      application.hasShownSniperIsWinning(auction, 1098)

      auction.announceClosed()
      application.showsSniperHasWonAcution(auction, 1098)
    }

    "bid for multiple items" in new after {
      auction.startSellingItem()
      auction2.startSellingItem()

      application.startBiddingIn(auction, auction2)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1000, 98, "other bidder")
      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

      auction2.reportPrice(500, 21, "other bidder")
      auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID)
      auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID)

      application.hasShownSniperIsWinning(auction, 1098)
      application.hasShownSniperIsWinning(auction2, 521)

      auction.announceClosed()
      auction2.announceClosed()

      application.showsSniperHasWonAcution(auction, 1098)
      application.showsSniperHasWonAcution(auction2, 521)
    }

    "lose an auction when the price is too high" in new after {
      auction.startSellingItem()
      application.startBiddingWithStopPrice(auction, 1100)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auction.reportPrice(1000, 98, "other bidder")
      application.hasShownSniperIsBidding(auction, 1000, 1098)

      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1197, 10, "third party")
      application.hasShownSniperIsLosing(auction, 1197, 1098)

      auction.reportPrice(1207, 10, "fourth party")
      application.hasShownSniperIsLosing(auction, 1207, 1098)

      auction.announceClosed()
      application.showsSniperHasLostAcution(auction, 1207, 1098)
    }

    "report invalid auction message and stop responding to events" in new after {
      val brokenMessage = "a broken mesage"
      auction.startSellingItem()
      auction2.startSellingItem()

      application.startBiddingIn(auction, auction2)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(500, 20, "other bidder")
      auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID)

      auction.sendInvalidMessageContaining(brokenMessage)
      application.showsSniperHasFailed(auction)

      auction.reportPrice(520, 21, "other bidder")
      waitForAnotherAuctionEvent()

      application.reportsInvalidMessage(auction, brokenMessage)
      application.showsSniperHasFailed(auction)
    }
  }
}

