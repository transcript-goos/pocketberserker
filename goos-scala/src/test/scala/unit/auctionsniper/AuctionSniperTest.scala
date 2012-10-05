package unit.auctionsniper

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import auctionsniper.{Auction, AuctionSniper, SniperListener}

class AuctionSniperTest extends Specification with Mockito {

  private val sniperListener = mock[SniperListener]
  private val auction = mock[Auction]
  private val sniper = new AuctionSniper(auction, sniperListener)

  "AuctionSniper" should {
    "reports lost when auction closes" in {
      sniper.auctionClosed()
      there was one(sniperListener).sniperLost()
    }

    "bids higher and reports bidding when new price arrives" in {
      val price = 1001
      val increment = 25
      sniper.currentPrice(price, increment)
      there was one(auction).bid(price + increment)
      there was atLeastOne(sniperListener).sniperBidding()
    }
  }
}
