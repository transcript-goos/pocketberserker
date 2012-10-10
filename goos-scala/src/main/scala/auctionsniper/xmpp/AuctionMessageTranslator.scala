package auctionsniper.xmpp

import org.jivesoftware.smack.{Chat, MessageListener}
import org.jivesoftware.smack.packet.Message
import auctionsniper.{FromOtherBidder, FromSniper, AuctionEventListener}

class AuctionMessageTranslator(
  private val sniperId: String,
  private val listener: AuctionEventListener) extends MessageListener {

  def processMessage(chat: Chat, message: Message) {
    try {
      translate(message.getBody)
    } catch {
      case parseError: Exception => listener.auctionFailed()
    }
  }

  private def translate(messageBody: String) {
    val event = AuctionEvent.from(messageBody)
    event.eventType match {
      case Close() => listener.auctionClosed()
      case Price(currentPrice, increment) =>
        listener.currentPrice(currentPrice, increment, event.isFrom(sniperId))
    }
  }

  private class AuctionEvent(val fields: Map[String, String]) {

    def eventType =
      fields.get("Event") match {
        case Some("CLOSE") => Close()
        case Some("PRICE") =>
          (fields.get("CurrentPrice"), fields.get("Increment")) match {
            case (Some(price), Some(increment)) => Price(price.toInt, increment.toInt)
            case other => throw new MissingValueException(other.toString)
          }
        case fieldName => throw new MissingValueException(fieldName.toString)
      }

    def isFrom(sniperId: String) =
      if(sniperId.equals(bidder)) FromSniper() else FromOtherBidder()

    def bidder = fields.get("Bidder") match {
      case Some(bidder) => bidder
      case other => throw new MissingValueException(other.toString)
    }
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

  private class MissingValueException(fieldName: String)
    extends Exception("Missing value for " + fieldName) {}
}

