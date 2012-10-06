package auctionsniper.ui

trait Column {
  def ordinal = Column.values.indexOf(this)
}

object Column {
  object ITEM_IDENTIFIER extends Column
  object LAST_PRICE extends Column
  object LAST_BID extends Column
  object SNIPER_STATUS extends Column

  val values = List(ITEM_IDENTIFIER, LAST_PRICE, LAST_BID, SNIPER_STATUS)

  def at(offset: Int) = values(offset)
}
