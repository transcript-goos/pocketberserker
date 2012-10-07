package unit.auctionsniper.xmpp

import org.specs2.mutable._
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import auctionsniper.{FromSniper, FromOtherBidder, AuctionEventListener}
import org.specs2.mock.Mockito
import auctionsniper.xmpp.AuctionMessageTranslator

object AuctionMessageTranslatorTest {
  val UNUSED_CHAT : Chat = null
  val SNIPER_ID = "sniper id"
}

class AuctionMessageTranslatorTest extends Specification with Mockito {

  import AuctionMessageTranslatorTest._

  val listener = mock[AuctionEventListener]
  val translator = new AuctionMessageTranslator(SNIPER_ID, listener)

  "AuctionMessageTransLator" should {

    "nofity auction closed when close message received" in {

      val message = new Message()
      message.setBody("SOLVersion: 1.1; Event: CLOSE;")

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).auctionClosed()
    }

    "nofitiy bid details when current price message received from other bidder" in {

      val message = new Message()
      message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
      )

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).currentPrice(192, 7, FromOtherBidder())
    }

    "notifiy bid details when current price message received from sniper" in {

      val message = new Message()
      message.setBody(
        "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";"
      )

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).currentPrice(234, 5, FromSniper())
    }
  }
}

