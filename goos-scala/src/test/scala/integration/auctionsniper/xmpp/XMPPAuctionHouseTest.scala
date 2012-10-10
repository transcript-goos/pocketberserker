package integration.auctionsniper.xmpp

import org.specs2.mutable.{BeforeAfter, Specification}
import java.util.concurrent.{TimeUnit, CountDownLatch}
import auctionsniper.xmpp.XMPPAuctionHouse
import end2end.auctionsniper.{ApplicationRunner, FakeAuctionServer}
import auctionsniper.{PriceSource, AuctionEventListener}

class XMPPAuctionHouseTest extends Specification {

  "XMPPAuction" should {
    "receive events from auction server after joining" in new beforeafter {
      val auctionWasClosed = new CountDownLatch(1)

      val auction = auctionHouse.auctionFor(auctionServer.itemId)
      auction += auctionClosedListener(auctionWasClosed)

      auction.join()
      auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auctionServer.announceClosed()

      auctionWasClosed.await(2, TimeUnit.SECONDS) must beTrue
    }
  }

  trait beforeafter extends BeforeAfter {
    val auctionHouse = XMPPAuctionHouse.connect(
      FakeAuctionServer.XMPP_HOSTNAME,
      ApplicationRunner.SNIPER_ID,
      ApplicationRunner.SNIPER_PASSWORD)
    val auctionServer = new FakeAuctionServer("item-54321")

    def before = auctionServer.startSellingItem()

    def after {
      auctionServer.stop()
      auctionHouse.disconnect()
    }
  }

  private def auctionClosedListener(auctionWasClosed: CountDownLatch): AuctionEventListener =
    new AuctionEventListener {
      def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {}
      def auctionClosed() { auctionWasClosed.countDown() }
    }
}
