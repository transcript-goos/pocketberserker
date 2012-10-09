package integration.auctionsniper.xmpp

import org.specs2.mutable.{BeforeAfter, Specification}
import java.util.concurrent.{TimeUnit, CountDownLatch}
import auctionsniper.xmpp.XMPPAuction
import end2end.auctionsniper.{ApplicationRunner, FakeAuctionServer}
import org.jivesoftware.smack.XMPPConnection
import auctionsniper.{PriceSource, AuctionEventListener}

class XMPPAuctionTest extends Specification {

  "XMPPAuction" should {
    "receive events from auction server after joining" in new beforeafter {
      val auctionWasClosed = new CountDownLatch(1)

      val auction = new XMPPAuction(connection, auctionServer.itemId)
      auction += auctionClosedListener(auctionWasClosed)

      auction.join()
      auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auctionServer.announceClosed()

      auctionWasClosed.await(2, TimeUnit.SECONDS) must beTrue
    }
  }

  trait beforeafter extends BeforeAfter {
    val connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME)
    val auctionServer = new FakeAuctionServer("item-54321")
    def before {
      connection.connect()
      connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD, XMPPAuction.AUCTION_RESOURCE)
      auctionServer.startSellingItem()
    }
    def after {
      auctionServer.stop()
      connection.disconnect()
    }
  }

  private def auctionClosedListener(auctionWasClosed: CountDownLatch): AuctionEventListener =
    new AuctionEventListener {
      def currentPrice(price: Int, increment: Int, priceSource: PriceSource) {}
      def auctionClosed() { auctionWasClosed.countDown() }
    }
}
