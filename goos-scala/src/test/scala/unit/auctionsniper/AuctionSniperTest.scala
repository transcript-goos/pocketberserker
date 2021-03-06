package unit.auctionsniper

import org.specs2.mutable.{Before, Specification}
import org.specs2.mock.Mockito
import auctionsniper._
import org.hamcrest.{FeatureMatcher, CoreMatchers}
import auctionsniper.SniperState._
import auctionsniper.FromOtherBidder
import auctionsniper.FromSniper

object AuctionSniperTest {
  val ITEM_ID: String = "item-id"
}

class AuctionSniperTest extends Specification {

  import AuctionSniperTest._

  trait mock extends Before with Mockito {
    lazy val sniperListener = mock[SniperListener]
    lazy val auction = mock[Auction]
    lazy val item = Item(ITEM_ID, 1234)
    lazy val sniper = new AuctionSniper(item, auction)
    def before = sniper += sniperListener

    def expectSniperToFailWhenItIs() {
      there was atLeast(1)(sniperListener).sniperStateChanged(
        SniperSnapshot(ITEM_ID, 0, 0, SniperState.FAILED)
      )
    }

    def allowingSniperBidding() {
      there was atLeast(0)(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(BIDDING)))
    }
  }

  "AuctionSniper" should {
    "report lost if auction closes immediately" in new mock {
      sniper.auctionClosed()
      there was atLeastOne(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(LOST)))
    }

    "bid higher and report bidding when new price arrives" in new mock {
      val price = 1001
      val increment = 25
      val bid = price + increment

      sniper.currentPrice(price, increment, FromOtherBidder())
      got {
        one(auction).bid(bid)
        atLeastOne(sniperListener).sniperStateChanged(SniperSnapshot(ITEM_ID, price, bid, BIDDING))
      }
    }

    "report is winning when current price comes from sniper" in new mock {
      sniper.currentPrice(123, 12, FromOtherBidder())
      sniper.currentPrice(135, 45, FromSniper())
      got {
        atLeast(0)(sniperListener).sniperStateChanged(anArgThat(aSniperThatIs(BIDDING)))
        atLeastOne(sniperListener).sniperStateChanged(
          new SniperSnapshot(ITEM_ID, 135, 135, WINNING)
        )
      }
    }

    "report lost if auction closes when bidding" in new mock {
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

    "not bid and reports losing if subsequent price is above stop price" in new mock {

      allowingSniperBidding()

      sniper.currentPrice(123, 45, FromOtherBidder())
      sniper.currentPrice(2345, 25, FromOtherBidder())

      got {
        val bid = 123 + 45
        atLeast(0)(auction).bid(bid)
        atLeast(1)(sniperListener).sniperStateChanged(
          SniperSnapshot(ITEM_ID, 2345, bid, LOSING)
        )
      }
    }

    "report failed if auction fails when bidding" in new mock {

      allowingSniperBidding()

      sniper.currentPrice(123, 45, FromOtherBidder())
      sniper.auctionFailed()

      expectSniperToFailWhenItIs()
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

