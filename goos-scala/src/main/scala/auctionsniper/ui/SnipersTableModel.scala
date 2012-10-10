package auctionsniper.ui

import javax.swing.table.AbstractTableModel
import auctionsniper._
import collection.mutable.ArrayBuffer
import com.objogate.exception.Defect
import scala.Some

object SnipersTableModel {
  private val STATUS_TEXT = Array("Joinning",
    "Bidding", "Winning", "Lost", "Won")

  def textFor(state: SniperState) = STATUS_TEXT(state.ordinal)
}

class SnipersTableModel extends AbstractTableModel with SniperListener with SniperCollector {

  private val snapshots = new ArrayBuffer[SniperSnapshot]
  private val notToBeGCd = new ArrayBuffer[AuctionSniper]

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

  def +=(sniper: AuctionSniper) {
    notToBeGCd += sniper
    addSniperSnapshot(sniper.getSnapshot)
    sniper += new SwingThreadSniperListener(this)
  }

  private def addSniperSnapshot(sniperSnapshot: SniperSnapshot) {
    snapshots += sniperSnapshot
    val row = snapshots.size - 1
    fireTableRowsInserted(row, row)
  }
}
