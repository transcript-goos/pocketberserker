package unit.auctionsniper

import org.specs2.mutable.Specification
import auctionsniper._
import org.specs2.mock.Mockito
import org.hamcrest.{Matchers, FeatureMatcher}

class SniperLauncherTest extends Specification with Mockito {

  private val auction = mock[Auction]
  private val auctionHouse = mock[AuctionHouse]
  private val collector = mock[SniperCollector]
  private val launcher = new SniperLauncher(auctionHouse, collector)

  "SniperLauncher" should {
    "add new sniper to collector and then joins auction" in {
      val itemId = "item-123"

      auctionHouse.auctionFor(itemId) returns auction

      launcher.joinAuction(itemId)

      got {
        atLeast(0)(auctionHouse).auctionFor(itemId)
        one(auction) += anArgThat(sniperForItem(itemId))
        one(collector) += anArgThat(sniperForItem(itemId))
        one(auction).join()
      }
    }
  }

  private def sniperForItem(itemId: String) = {
    new FeatureMatcher[AuctionSniper, String](Matchers.equalTo(itemId), "sniper that is ", "was") {
      def featureValueOf(actual: AuctionSniper) = actual.getSnapshot.itemId
    }
  }
}

