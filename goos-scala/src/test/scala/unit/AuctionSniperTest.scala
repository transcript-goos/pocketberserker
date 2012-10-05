package unit

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import auctionsniper.{AuctionSniper, SniperListener}

class AuctionSniperTest extends Specification with Mockito {

  private val sniperListener = mock[SniperListener]
  private val sniper = new AuctionSniper(sniperListener)

  "AuctionSniper" should {
    "reports lost when auction closes" in {
      sniper.auctionClosed()
      there was one(sniperListener).sniperLost()
    }
  }
}
