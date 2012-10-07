package auctionsniper.ui

import javax.swing.table.AbstractTableModel
import auctionsniper.{SniperState, SniperSnapshot}
import auctionsniper.ui.Column.{SNIPER_STATE, LAST_BID, LAST_PRICE, ITEM_IDENTIFIER}

object SnipersTableModel {
  private val STARTING_UP = SniperSnapshot("", 0, 0, SniperState.JOINNING)
  private val STATUS_TEXT = Array("Joinning",
    "Bidding", "Winning", "Lost", "Won")

  def textFor(state: SniperState) = STATUS_TEXT(state.ordinal)
}

class SnipersTableModel extends AbstractTableModel {

  import SnipersTableModel._

  private var snapshot = STARTING_UP

  def getRowCount = 1
  def getColumnCount = Column.values.length
  def getValueAt(rowIndex: Int, columnIndex: Int) = {
    Column.at(columnIndex) match {
      case ITEM_IDENTIFIER => snapshot.itemId
      case LAST_PRICE => snapshot.lastPrice.asInstanceOf[Object]
      case LAST_BID => snapshot.lastBid.asInstanceOf[Object]
      case SNIPER_STATE => textFor(snapshot.state)
    }
  }

  def sniperStateChanged(newSnapshot: SniperSnapshot) {
    snapshot = newSnapshot
    fireTableRowsUpdated(0, 0)
  }
}
