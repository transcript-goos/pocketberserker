package auctionsniper

import org.jivesoftware.smack.{Chat, MessageListener}
import org.jivesoftware.smack.packet.Message

class AuctionMessageTranslator(private val listener: AuctionEventListener) extends MessageListener {

  def processMessage(chat: Chat, message: Message) {
    listener.auctionClosed()
  }
}
