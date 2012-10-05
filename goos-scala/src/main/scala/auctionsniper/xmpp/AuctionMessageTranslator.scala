package auctionsniper.xmpp

import org.jivesoftware.smack.{Chat, MessageListener}
import org.jivesoftware.smack.packet.Message
import auctionsniper.AuctionEventListener

class AuctionMessageTranslator(private val listener: AuctionEventListener) extends MessageListener {

  def processMessage(chat: Chat, message: Message) {
    val event = AuctionEvent.from(message.getBody)
    event.eventType match {
      case "CLOSE" => listener.auctionClosed()
      case "PRICE" =>
        listener.currentPrice(event.currentPrice, event.increment)
      case _ => ()
    }
  }

  private class AuctionEvent {
    val fields = new collection.mutable.HashMap[String, String]()

    def eventType = get("Event")
    def currentPrice = get("CurrentPrice").toInt
    def increment = get("Increment").toInt

    private def get(fieldName: String) = {
      val value = fields(fieldName)
      if(value == null) throw new Exception("Missing value for " + fieldName)
      value
    }

    private def addField(field: String) {
      val pair = field.split(":")
      fields += (pair(0).trim -> pair(1).trim)
    }
  }

  private object AuctionEvent {
    def fieldIn(messageBody: String) = messageBody.split(";")
    def from(messageBody: String) = {
      val event = new AuctionEvent()
      for (field <- fieldIn(messageBody))
        event.addField(field)
      event
    }
  }
}
