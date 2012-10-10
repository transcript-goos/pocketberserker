package auctionsniper

import java.util.EventListener

trait AuctionEventListener extends EventListener {
  def auctionClosed()
  def auctionFailed()
  def currentPrice(price: Int, increment: Int, priceSource: PriceSource)
}

trait PriceSource {}
case class FromSniper() extends PriceSource
case class FromOtherBidder() extends PriceSource

