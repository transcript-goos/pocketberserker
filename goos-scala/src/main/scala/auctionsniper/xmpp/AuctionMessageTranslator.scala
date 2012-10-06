package auctionsniper.xmpp

import org.jivesoftware.smack.{Chat, MessageListener}
import org.jivesoftware.smack.packet.Message
import auctionsniper.{FromOtherBidder, FromSniper, AuctionEventListener}

class AuctionMessageTranslator(
  private val sniperId: String,
  private val listener: AuctionEventListener) extends MessageListener {

  def processMessage(chat: Chat, message: Message) {
    val event = AuctionEvent.from(message.getBody)
    event.eventType match {
      case Close() => listener.auctionClosed()
      case Price(currentPrice, increment) =>
        listener.currentPrice(currentPrice, increment, event.isFrom(sniperId))
    }
  }

  private class AuctionEvent(val fields: Map[String, String]) {

    def eventType =
      fields("Event") match {
        case "CLOSE" => Close()
        case "PRICE" => Price(fields("CurrentPrice").toInt, fields("Increment").toInt)
        case other => throw new Exception("Missing value for" + other)
      }

    def isFrom(sniperId: String) =
      if(sniperId.equals(bidder)) FromSniper() else FromOtherBidder()

    def bidder = fields("Bidder")
  }

  private object AuctionEvent {

    private def fieldIn(messageBody: String) = messageBody.split(";")

    private def createField(field: String) = {
      val pair = field.split(":")
      (pair(0).trim -> pair(1).trim)
    }

    def from(messageBody: String) =
      new AuctionEvent(fieldIn(messageBody).foldLeft(Map.empty[String, String])(_ + createField(_)))
  }

  trait EventType {}
  case class Close() extends EventType
  case class Price(currentPrice: Int, increment: Int) extends EventType
}

