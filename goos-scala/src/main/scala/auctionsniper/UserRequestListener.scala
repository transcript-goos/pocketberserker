package auctionsniper

import java.util.EventListener

trait UserRequestListener extends EventListener {
  def joinAuction(item: Item)
}

