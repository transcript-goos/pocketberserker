package unit.auctionsniper.xmpp

import org.specs2.mutable._
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import auctionsniper.{FromSniper, FromOtherBidder, AuctionEventListener}
import org.specs2.mock.Mockito
import auctionsniper.xmpp.{XMPPFailureReporter, AuctionMessageTranslator}
import org.specs2.specification.Scope
import end2end.auctionsniper.ApplicationRunner

object AuctionMessageTranslatorTest {
  val UNUSED_CHAT : Chat = null
  val SNIPER_ID = "sniper id"
}

class AuctionMessageTranslatorTest extends Specification {

  import AuctionMessageTranslatorTest._

  trait mock extends Scope with Mockito {
    val listener = mock[AuctionEventListener]
    val failureReport = mock[XMPPFailureReporter]
    val translator = new AuctionMessageTranslator(SNIPER_ID, listener)

    def expectFailureWithMessage(badMessage: String) {
      got {
        one(listener).auctionFailed()
        one(failureReport).cannotTranslateMessage(
          argThat(===(ApplicationRunner.SNIPER_ID)),
          argThat(===(badMessage)),
          argThat(===(any[Exception]))
        )
      }
    }
  }

  "AuctionMessageTransLator" should {

    "nofity auction closed when close message received" in new mock {

      val message = new Message()
      message.setBody("SOLVersion: 1.1; Event: CLOSE;")

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).auctionClosed()
    }

    "nofitiy bid details when current price message received from other bidder" in new mock {

      val message = new Message()
      message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
      )

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).currentPrice(192, 7, FromOtherBidder())
    }

    "notifiy bid details when current price message received from sniper" in new mock {

      val message = new Message()
      message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";"
      )

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).currentPrice(234, 5, FromSniper())
    }

    "notify auction failed when bad message received" in new mock {
      val badMessage = "a bad message"

      translator.processMessage(UNUSED_CHAT, message(badMessage))

      expectFailureWithMessage(badMessage)
    }

    "notify auction failed when event type missing" in new mock {
      val badMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";"

      translator.processMessage(UNUSED_CHAT, message(badMessage))

      expectFailureWithMessage(badMessage)
    }
  }

  private def message(messageBody: String) = {
    val message = new Message()
    message.setBody(messageBody)
    message
  }
}

