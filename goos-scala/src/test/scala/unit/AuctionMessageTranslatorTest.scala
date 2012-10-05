package unit

import org.specs2.mutable._
import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import auctionsniper.{AuctionEventListener, AuctionMessageTranslator}
import org.specs2.mock.Mockito

object AuctionMessageTranslatorTest {
  val UNUSED_CHAT : Chat = null
}

class AuctionMessageTranslatorTest extends Specification with Mockito {

  import AuctionMessageTranslatorTest.UNUSED_CHAT

  val listener = mock[AuctionEventListener]
  val translator = new AuctionMessageTranslator(listener)

  "AuctionMessageTransLator" should {

    "nofities auction closed when close message received" in {

      val message = new Message()
      message.setBody("SOLVersion: 1.1; Event: CLOSE;")

      translator.processMessage(UNUSED_CHAT, message)

      there was one(listener).auctionClosed()
    }
  }
}

