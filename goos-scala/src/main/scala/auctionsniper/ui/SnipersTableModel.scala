package auctionsniper.ui

import javax.swing.table.AbstractTableModel

class SnipersTableModel extends AbstractTableModel {

  private var statusText = MainWindow.STATUS_JOINING

  def getRowCount = 1
  def getColumnCount = 1
  def getValueAt(rowIndex: Int, columnIndex: Int) = statusText

  def setStatusText(newStatusText: String) {
    statusText = newStatusText
    fireTableRowsUpdated(0, 0)
  }
}
