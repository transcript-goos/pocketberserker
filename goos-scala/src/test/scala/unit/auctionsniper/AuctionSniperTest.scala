package unit.auctionsniper

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import auctionsniper._
import org.hamcrest.{FeatureMatcher, CoreMatchers}
import auctionsniper.SniperState.{WON, LOST, WINNING, BIDDING}
import org.specs2.specification.Scope

object AuctionSniperTest {
  val ITEM_ID: String = "item-id"
}

class AuctionSniperTest extends Specification {

  import AuctionSniperTest._

  trait mock extends Scope with Mockito {
    lazy val sniperListener = mock[SniperListener]
    lazy val auction = mock[Auction]
    lazy val sniper = new AuctionSniper(ITEM_ID, auction, sniperListener)
  }

  "AuctionSniper" should {
    "reports lost if auction closes immediately" in new mock {
      sniper.auctionClosed()
      there was atLeastOne(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(LOST)))
    }

    "bids higher and reports bidding when new price arrives" in new mock {
      val price = 1001
      val increment = 25
      val bid = price + increment

      sniper.currentPrice(price, increment, FromOtherBidder())
      got {
        one(auction).bid(bid)
        atLeastOne(sniperListener).sniperStateChanged(SniperSnapshot(ITEM_ID, price, bid, BIDDING))
      }
    }

    "reports is winning when current price comes from sniper" in new mock {
      sniper.currentPrice(123, 12, FromOtherBidder())
      sniper.currentPrice(135, 45, FromSniper())
      got {
        atLeast(0)(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(BIDDING)))
        atLeastOne(sniperListener).sniperStateChanged(
          new SniperSnapshot(ITEM_ID, 135, 135, WINNING)
        )
      }
    }

    "reports lost if auction closes when bidding" in new mock {
      sniper.currentPrice(123, 45, FromOtherBidder())
      sniper.auctionClosed()
      got {
        atLeast(0)(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(BIDDING)))
        atLeastOne(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(LOST)))
      }
    }

    "report won if auction closes when winning" in new mock {
      sniper.currentPrice(123, 45, FromSniper())
      sniper.auctionClosed()
      got {
        atLeast(0)(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(WINNING)))
        atLeastOne(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(WON)))
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

