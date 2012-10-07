package auctionsniper.ui

import auctionsniper.SniperSnapshot

trait Column {
  def ordinal = Column.values.indexOf(this)
  def valueIn(snapshot: SniperSnapshot) : Object
}

object Column {
  object ITEM_IDENTIFIER extends Column {
    def valueIn(snapshot: SniperSnapshot) = snapshot.itemId
  }
  object LAST_PRICE extends Column {
    def valueIn(snapshot: SniperSnapshot) = snapshot.lastPrice.asInstanceOf[Object]
  }
  object LAST_BID extends Column {
    def valueIn(snapshot: SniperSnapshot) = snapshot.lastBid.asInstanceOf[Object]
  }
  object SNIPER_STATE extends Column {
    def valueIn(snapshot: SniperSnapshot) = SnipersTableModel.textFor(snapshot.state)
  }

  val values = List(ITEM_IDENTIFIER, LAST_PRICE, LAST_BID, SNIPER_STATE)

  def at(offset: Int) = values(offset)
}
