package unit.auctionsniper

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import auctionsniper._
import org.hamcrest.{FeatureMatcher, CoreMatchers}
import auctionsniper.SniperState.BIDDING

object AuctionSniperTest {
  val ITEM_ID: String = "item-id"
}

class AuctionSniperTest extends Specification with Mockito {

  import AuctionSniperTest._

  private val sniperListener = mock[SniperListener]
  private val auction = mock[Auction]
  private val sniper = new AuctionSniper(ITEM_ID, auction, sniperListener)

  "AuctionSniper" should {
    "reports lost if auction closes immediately" in {
      sniper.auctionClosed()
      there was atLeastOne(sniperListener).sniperLost()
    }

    "bids higher and reports bidding when new price arrives" in {
      val price = 1001
      val increment = 25
      val bid = price + increment

      sniper.currentPrice(price, increment, FromOtherBidder())
      there was one(auction).bid(bid)
      there was atLeastOne(sniperListener).sniperStateChanged(SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING))
    }

    "reports is winning when current price comes from sniper" in {
      sniper.currentPrice(123, 45, FromSniper())
      there was atLeastOne(sniperListener).sniperWinning()
    }

    "reports lost if auction closes when bidding" in {
      sniper.currentPrice(123, 45, FromOtherBidder())
      sniper.auctionClosed()
      got {
        atLeast(0)(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(BIDDING)))
        atLeastOne(sniperListener).sniperLost()
      }
    }

    "report won if auction closes when winning" in {
      sniper.currentPrice(123, 45, FromSniper())
      sniper.auctionClosed()
      got {
        atLeast(0)(sniperListener).sniperWinning()
        atLeastOne(sniperListener).sniperWon()
      }
    }
  }

  private def aSniperThatIs(state: SniperState) = {
    new FeatureMatcher[SniperSnapshot, SniperState](
      CoreMatchers.equalTo(state), "sniper that is", "was") {
      def featureValueOf(actual: SniperSnapshot) =
          actual.state
    }
  }
}

