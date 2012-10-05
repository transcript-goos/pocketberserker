package auctionsniper

import org.jivesoftware.smack.{Chat, MessageListener}
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator(private val listener: AuctionEventListener) extends MessageListener {

  def processMessage(chat: Chat, message: Message) {
    val event = unpackEventFrom(message)
    event("Event") match {
      case "CLOSE" => listener.auctionClosed()
      case "PRICE" =>
        listener.currentPrice(event("CurrentPrice").toInt, event("Increment").toInt)
      case _ => ()
    }
  }

  private def unpackEventFrom(message: Message) = {
    message.getBody.split(";").foldLeft(Map.empty[String, String]){
      (event, element) =>
        val pair = element.split(":")
        event + (pair(0).trim -> pair(1).trim)
    }
  }
}
