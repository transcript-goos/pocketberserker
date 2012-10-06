package auctionsniper.ui

import javax.swing.table.AbstractTableModel
import auctionsniper.SniperSnapshot
import auctionsniper.ui.Column.{SNIPER_STATUS, LAST_BID, LAST_PRICE, ITEM_IDENTIFIER}

object SnipersTableModel {
  private val STARTING_UP = SniperSnapshot("", 0, 0)
}

class SnipersTableModel extends AbstractTableModel {

  import SnipersTableModel._

  private var statusText = MainWindow.STATUS_JOINING
  private var sniperState = STARTING_UP

  def getRowCount = 1
  def getColumnCount = Column.values.length
  def getValueAt(rowIndex: Int, columnIndex: Int) = {
    Column.at(columnIndex) match {
      case ITEM_IDENTIFIER => sniperState.itemId
      case LAST_PRICE => sniperState.lastPrice.asInstanceOf[Object]
      case LAST_BID => sniperState.lastBid.asInstanceOf[Object]
      case SNIPER_STATUS => statusText
    }
  }

  def setStatusText(newStatusText: String) {
    statusText = newStatusText
    fireTableRowsUpdated(0, 0)
  }

  def sniperStatusChanged(newSniperSnapshot: SniperSnapshot, newStatusText: String) {
    sniperState = newSniperSnapshot
    statusText = newStatusText
    fireTableRowsUpdated(0, 0)
  }
}
