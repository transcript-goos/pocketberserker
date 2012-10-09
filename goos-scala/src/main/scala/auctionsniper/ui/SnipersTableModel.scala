package auctionsniper.ui

import javax.swing.table.AbstractTableModel
import auctionsniper.{SniperListener, SniperState, SniperSnapshot}
import collection.mutable.ArrayBuffer
import com.objogate.exception.Defect

object SnipersTableModel {
  private val STATUS_TEXT = Array("Joinning",
    "Bidding", "Winning", "Lost", "Won")

  def textFor(state: SniperState) = STATUS_TEXT(state.ordinal)
}

class SnipersTableModel extends AbstractTableModel with SniperListener {

  private var snapshots = new ArrayBuffer[SniperSnapshot]

  def getRowCount = snapshots.size

  def getColumnCount = Column.values.size

  def getValueAt(rowIndex: Int, columnIndex: Int) =
    Column.at(columnIndex).valueIn(snapshots(rowIndex))

  def sniperStateChanged(newSnapshot: SniperSnapshot) {
    val row = rowMatching(newSnapshot)
    snapshots.update(row, newSnapshot)
    fireTableRowsUpdated(row, row)
  }

  override def getColumnName(column: Int) =
    Column.at(column).name

  def +=(snapshot: SniperSnapshot) {
    snapshots += snapshot
    fireTableRowsInserted(snapshots.size - 1, snapshots.size - 1)
  }

  private def rowMatching(snapshot: SniperSnapshot) : Int = {
    snapshots.zipWithIndex.find{
      case (s: SniperSnapshot, _: Int) => snapshot.isForSameItemAs(s)
    } match {
      case Some((_, i)) => i
      case None => throw new Defect("Cannot find match for " + snapshot)
    }
  }
}
